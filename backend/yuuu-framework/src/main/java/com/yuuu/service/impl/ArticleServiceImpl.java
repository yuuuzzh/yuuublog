package com.yuuu.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuuu.constants.SystemConstants;
import com.yuuu.domain.ResponseResult;
import com.yuuu.domain.dto.ArticleDto;
import com.yuuu.domain.dto.ArticleListDto;
import com.yuuu.domain.entity.Article;
import com.yuuu.domain.entity.ArticleTag;
import com.yuuu.domain.entity.Category;
import com.yuuu.domain.vo.*;
import com.yuuu.enums.AppHttpCodeEnum;
import com.yuuu.mapper.ArticleMapper;
import com.yuuu.service.ArticleService;
import com.yuuu.service.ArticleTagService;
import com.yuuu.service.CategoryService;
import com.yuuu.service.LikedService;
import com.yuuu.utils.BeanCopyUtils;
import com.yuuu.utils.BlogCacheManager;
import com.yuuu.utils.RedisCache;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static com.yuuu.constants.SystemConstants.ARTICLE_STATUS_NORMAL;

/**
 * @BelongsProject:YuuuBlog
 * @BelongsPackage:com.yuuu.service.impl
 * @Author:Yuuu。
 * @CreateTime:2023-03-02 14:47
 * @Description:
 * @Version:1.0
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    private static final Logger log = LoggerFactory.getLogger(ArticleServiceImpl.class);

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ArticleTagService articleTagService;

    @Autowired
    private LikedService likedService;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private BlogCacheManager blogCacheManager;

    @Override
    public ResponseResult hotArticleList() {
        // 热门文章：读穿缓存
        Object hotCache = blogCacheManager.get(SystemConstants.HOT_ARTICLE_KEY, List.class);
        if (hotCache != null) {
            return ResponseResult.okResult(hotCache);
        }
        // 查询热门文章，返回ResponseResult结果
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        // 1.正式发布
        wrapper.eq(Article::getStatus, ARTICLE_STATUS_NORMAL);
        // 2.按浏览量降序排列
        wrapper.orderByDesc(Article::getViewCount);
        // 3.最多查询10条数据
        Page<Article> page = new Page<>(1, 10);
        page(page, wrapper);
        List<Article> articles = page.getRecords();
        // bean复制
        List<HotArticleVo> articleVoList = BeanCopyUtils.copyBeanList(articles, HotArticleVo.class);
        blogCacheManager.set(SystemConstants.HOT_ARTICLE_KEY, articleVoList, SystemConstants.CACHE_TTL_SHORT);
        return ResponseResult.okResult(articleVoList);
    }

    @Override
    public ResponseResult articleList(Integer pageNum, Integer pageSize, Long categoryId, Long tagId) {
        // 文章列表：按 分类+标签+分页 维度缓存
        String listKey = SystemConstants.ARTICLE_LIST_KEY + "c" + categoryId + ":t" + tagId + ":" + pageNum + ":" + pageSize;
        Object listCache = blogCacheManager.get(listKey, PageVo.class);
        if (listCache != null) {
            return ResponseResult.okResult(listCache);
        }
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        // 标签筛选：先取该标签下的文章 id（空结果也缓存，避免每次都查一遍关联表）
        if (tagId != null && tagId > 0) {
            List<Long> articleIds = articleTagService
                    .list(new LambdaQueryWrapper<ArticleTag>().eq(ArticleTag::getTagId, tagId))
                    .stream().map(ArticleTag::getArticleId).collect(Collectors.toList());
            if (articleIds.isEmpty()) {
                PageVo empty = new PageVo(new ArrayList<ArticleListVo>(), 0L);
                blogCacheManager.set(listKey, empty, SystemConstants.CACHE_TTL_SHORT);
                return ResponseResult.okResult(empty);
            }
            queryWrapper.in(Article::getId, articleIds);
        }
        // 分类条件
        queryWrapper.eq(Objects.nonNull(categoryId) && categoryId > 0, Article::getCategoryId, categoryId);
        // 正式发布的文章
        queryWrapper.eq(Article::getStatus, SystemConstants.CATEGORY_STATUS_NORMAL);
        // 是否置顶，置顶的按访问量排
        queryWrapper.orderByDesc(Article::getIsTop).orderByDesc(Article::getViewCount);
        Page page = new Page(pageNum, pageSize);
        page(page, queryWrapper);
        // 查询categoryName
        List<Article> records = page.getRecords();
        // 根据id去查name
        records = records.stream()
                .map(article -> {
                    Category category = categoryService.getById(article.getCategoryId());
                    article.setCategoryName(category.getName());
                    return article;
                })
                .collect(Collectors.toList());
        // 封装查询结果
        List<ArticleListVo> articleListVos = BeanCopyUtils.copyBeanList(records, ArticleListVo.class);
        PageVo pageVo = new PageVo(articleListVos, page.getTotal());
        blogCacheManager.set(listKey, pageVo, SystemConstants.CACHE_TTL_SHORT);
        return ResponseResult.okResult(pageVo);
    }

    @Override
    public ResponseResult archives() {
        // 归档：按年月聚合已发布文章
        List<ArchiveVo> cache = blogCacheManager.get(SystemConstants.ARCHIVE_KEY, List.class);
        if (cache != null) {
            return ResponseResult.okResult(cache);
        }
        List<Article> articles = list(new LambdaQueryWrapper<Article>()
                .eq(Article::getStatus, SystemConstants.CATEGORY_STATUS_NORMAL)
                .orderByDesc(Article::getCreateTime));
        // 按 yyyy-MM 分组，保持时间倒序
        Map<String, List<Article>> groupMap = articles.stream()
                .collect(Collectors.groupingBy(
                        article -> article.getCreateTime() == null ? "未知时间" : new SimpleDateFormat("yyyy-MM").format(article.getCreateTime()),
                        LinkedHashMap::new,
                        Collectors.toList()));
        List<ArchiveVo> vos = new ArrayList<>();
        for (Map.Entry<String, List<Article>> entry : groupMap.entrySet()) {
            List<ArchiveArticleVo> items = entry.getValue().stream()
                    .map(article -> new ArchiveArticleVo(article.getId(), article.getTitle(), article.getCreateTime()))
                    .collect(Collectors.toList());
            vos.add(new ArchiveVo(entry.getKey(), items.size(), items));
        }
        blogCacheManager.set(SystemConstants.ARCHIVE_KEY, vos, SystemConstants.CACHE_TTL_LONG);
        return ResponseResult.okResult(vos);
    }

    @Override
    public ResponseResult articleDetailById(Long id) {
        // 文章详情：读穿缓存
        String detailKey = SystemConstants.ARTICLE_DETAIL_KEY + id;
        ArticleDetailVo detailCache = blogCacheManager.get(detailKey, ArticleDetailVo.class);
        if (detailCache != null) {
            return ResponseResult.okResult(detailCache);
        }
        // 查询文章
        Article article = getById(id);
        if (Objects.isNull(article)) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NO_ARTICLE);
        }
        // 从redis中查询viewCount，Redis 异常时降级用库里的值
        Integer viewCount = null;
        try {
            viewCount = redisCache.getCacheMapValue(SystemConstants.VIEW_COUNT, id.toString());
        } catch (Exception e) {
            log.warn("读取浏览量缓存失败，降级使用库内浏览量：articleId={}, err={}", id, e.getMessage());
        }
        if (viewCount == null) {
            viewCount = article.getViewCount() == null ? 0 : Integer.parseInt(String.valueOf(article.getViewCount()));
        }
        // 封装Vo
        ArticleDetailVo articleDetailVo = BeanCopyUtils.copyBean(article, ArticleDetailVo.class);
        articleDetailVo.setViewCount(viewCount.longValue());
        // 查询分类名
        Long categoryId = articleDetailVo.getCategoryId();
        Category category = categoryService.getById(categoryId);
        if (category != null) {
            articleDetailVo.setCategoryName(category.getName());
        }
        // 上下篇（按发布时间，仅统计已发布文章）
        fillNeighbors(article, articleDetailVo);
        blogCacheManager.set(detailKey, articleDetailVo, SystemConstants.CACHE_TTL_SHORT);
        return ResponseResult.okResult(articleDetailVo);
    }

    /**
     * 填充上一篇/下一篇
     */
    private void fillNeighbors(Article current, ArticleDetailVo vo) {
        if (current.getCreateTime() == null) {
            return;
        }
        LambdaQueryWrapper<Article> prevWrapper = new LambdaQueryWrapper<>();
        prevWrapper.eq(Article::getStatus, SystemConstants.CATEGORY_STATUS_NORMAL)
                .lt(Article::getCreateTime, current.getCreateTime())
                .orderByDesc(Article::getCreateTime)
                .last("limit 1");
        Article prev = getOne(prevWrapper);
        if (prev != null) {
            vo.setPrevId(prev.getId());
            vo.setPrevTitle(prev.getTitle());
        }
        LambdaQueryWrapper<Article> nextWrapper = new LambdaQueryWrapper<>();
        nextWrapper.eq(Article::getStatus, SystemConstants.CATEGORY_STATUS_NORMAL)
                .gt(Article::getCreateTime, current.getCreateTime())
                .orderByAsc(Article::getCreateTime)
                .last("limit 1");
        Article next = getOne(nextWrapper);
        if (next != null) {
            vo.setNextId(next.getId());
            vo.setNextTitle(next.getTitle());
        }
    }

    @Override
    public ResponseResult relatedArticles(Long articleId, Integer size) {
        int n = (size == null || size <= 0) ? 6 : Math.min(size, 20);
        List<RelatedArticleVo> result = new ArrayList<>();
        Article current = getById(articleId);
        if (current != null && current.getCategoryId() != null) {
            LambdaQueryWrapper<Article> sameCategory = new LambdaQueryWrapper<>();
            sameCategory.eq(Article::getStatus, SystemConstants.CATEGORY_STATUS_NORMAL)
                    .eq(Article::getCategoryId, current.getCategoryId())
                    .ne(Article::getId, articleId)
                    .orderByDesc(Article::getCreateTime)
                    .last("limit " + n);
            result.addAll(toRelatedVo(list(sameCategory)));
        }
        // 同分类不足时用热门文章补齐
        if (result.size() < n) {
            List<Long> existIds = new ArrayList<>();
            existIds.add(articleId);
            for (RelatedArticleVo vo : result) {
                existIds.add(vo.getId());
            }
            LambdaQueryWrapper<Article> hot = new LambdaQueryWrapper<>();
            hot.eq(Article::getStatus, SystemConstants.CATEGORY_STATUS_NORMAL)
                    .notIn(!existIds.isEmpty(), Article::getId, existIds)
                    .orderByDesc(Article::getViewCount)
                    .last("limit " + (n - result.size()));
            result.addAll(toRelatedVo(list(hot)));
        }
        return ResponseResult.okResult(result);
    }

    @Override
    public ResponseResult searchArticles(String keyword, Integer pageNum, Integer pageSize) {
        if (!StringUtils.hasText(keyword)) {
            return ResponseResult.okResult(new PageVo(new ArrayList<SearchArticleVo>(), 0L));
        }
        String kw = keyword.trim();
        int pn = (pageNum == null || pageNum < 1) ? 1 : pageNum;
        int ps = (pageSize == null || pageSize < 1) ? 10 : Math.min(pageSize, 50);
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Article::getStatus, SystemConstants.CATEGORY_STATUS_NORMAL)
                .and(w -> w.like(Article::getTitle, kw).or().like(Article::getContent, kw))
                .orderByDesc(Article::getCreateTime);
        Page<Article> page = new Page<>(pn, ps);
        page(page, wrapper);

        List<SearchArticleVo> vos = new ArrayList<>();
        for (Article article : page.getRecords()) {
            SearchArticleVo vo = new SearchArticleVo();
            vo.setId(article.getId());
            vo.setTitle(article.getTitle());
            vo.setExcerpt(buildExcerpt(article.getContent(), kw));
            vo.setCategoryId(article.getCategoryId());
            vo.setCreateTime(article.getCreateTime());
            vo.setViewCount(article.getViewCount());
            vos.add(vo);
        }
        return ResponseResult.okResult(new PageVo(vos, page.getTotal()));
    }

    /**
     * 取关键词附近的正文片段，并去掉常见 Markdown 标记，便于前端展示与高亮
     */
    private String buildExcerpt(String content, String keyword) {
        if (!StringUtils.hasText(content)) {
            return "";
        }
        String plain = content
                .replaceAll("```[\\s\\S]*?```", " ")
                .replaceAll("!\\[[^\\]]*\\]\\([^)]*\\)", " ")
                .replaceAll("[^\\]]*\\]\\[[^)]*\\)", " ")
                .replaceAll("[#>*`\\-\\|\\[\\]()]", " ")
                .replaceAll("\\s+", " ")
                .trim();
        int idx = plain.toLowerCase().indexOf(keyword.toLowerCase());
        int len = plain.length();
        int radius = 60;
        if (idx < 0) {
            return len > 120 ? plain.substring(0, 120) + "…" : plain;
        }
        int start = Math.max(0, idx - radius);
        int end = Math.min(len, idx + keyword.length() + radius);
        return (start > 0 ? "…" : "") + plain.substring(start, end) + (end < len ? "…" : "");
    }

    private List<RelatedArticleVo> toRelatedVo(List<Article> articles) {
        List<RelatedArticleVo> vos = new ArrayList<>();
        for (Article article : articles) {
            RelatedArticleVo vo = new RelatedArticleVo();
            vo.setId(article.getId());
            vo.setTitle(article.getTitle());
            vo.setCreateTime(article.getCreateTime());
            vo.setViewCount(article.getViewCount());
            vos.add(vo);
        }
        return vos;
    }


    @Override
    public ResponseResult updateViewCount(Long articleId) {
        try {
            redisCache.incrementCacheMapValue(SystemConstants.VIEW_COUNT, articleId.toString(), 1);
        } catch (Exception e) {
            // 浏览量只是统计值，Redis 异常时丢弃这次累加，不影响文章访问
            log.warn("累加浏览量失败，本次不计：articleId={}, err={}", articleId, e.getMessage());
        }
        return ResponseResult.okResult();
    }

    @Override
    @Transactional
    public ResponseResult add(ArticleDto articleDto) {
        if (StrUtil.isBlank(articleDto.getTitle()) || StrUtil.isBlank(articleDto.getContent())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NOT_NULL);
        }
        // 添加博客
        Article article = BeanCopyUtils.copyBean(articleDto, Article.class);
        save(article);

        // 根据标签id获取文章对应标签集合
        List<ArticleTag> articleTags = articleDto.getTags().stream()
                .map(tagId -> new ArticleTag(article.getId(), tagId))
                .collect(Collectors.toList());

        // 添加 博客和标签的关联
        articleTagService.saveBatch(articleTags);
        // 新增后失效前台缓存：列表/详情/热门/分类（分类列表源自发布文章）
        blogCacheManager.evictArticle(article.getId());
        blogCacheManager.evictCategory();
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult articleListAdmin(Integer pageNum, Integer pageSize, ArticleListDto articleListDto) {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(articleListDto.getTitle())) {
            queryWrapper.like(Article::getTitle, articleListDto.getTitle());
        }
        if (StringUtils.hasText(articleListDto.getSummary())) {
            queryWrapper.like(Article::getSummary, articleListDto.getSummary());
        }
        Page<Article> page = new Page<>(pageNum, pageSize);
        page(page, queryWrapper);
        List<Article> records = page.getRecords();
        List<ArticleListAdminVo> articleListAdminVos = BeanCopyUtils.copyBeanList(records, ArticleListAdminVo.class);
        return ResponseResult.okResult(new PageVo(articleListAdminVos, page.getTotal()));
    }

    @Override
    public ResponseResult getArticleById(Long id) {
        Article article = getById(id);
        LambdaQueryWrapper<ArticleTag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ArticleTag::getArticleId, id);
        // 根据文章id查询文章标签集合
        List<ArticleTag> list = articleTagService.list(queryWrapper);
        // 得到标签id的集合
        List<Long> tags = list.stream().map(ArticleTag::getTagId)
                .collect(Collectors.toList());
        // 封装Vo返回
        ArticleByIdVo articleByIdVo = new ArticleByIdVo();
        BeanUtils.copyProperties(article, articleByIdVo);
        articleByIdVo.setTags(tags);
        return ResponseResult.okResult(articleByIdVo);
    }

    @Override
    @Transactional
    public ResponseResult updateArticle(ArticleByIdVo article) {
        Article article1 = BeanCopyUtils.copyBean(article, Article.class);
        updateById(article1);
        // 先删除以前的，再建立新的
        List<Long> tags = article.getTags();
        if (Objects.nonNull(tags) && tags.size() > 0) {
            baseMapper.deleteArticleTagById(article.getId());
            baseMapper.insertArticleTag(article.getId(), tags);
        } else {
            baseMapper.deleteArticleTagById(article.getId());
        }
        // 修改后失效前台缓存（标题/置顶/状态/分类都可能在本次变更）
        blogCacheManager.evictArticle(article.getId());
        blogCacheManager.evictCategory();
        return ResponseResult.okResult();

    }

    @Override
    public ResponseResult deleteArticle(Long id) {
        boolean b = removeById(id);
        if (b) {
            // 逻辑删除后必须失效前台缓存，否则已删文章仍会出现在列表/详情
            blogCacheManager.evictArticle(id);
            blogCacheManager.evictCategory();
            return ResponseResult.okResult();
        } else {
            return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR);
        }
    }

    @Override
    public ResponseResult getArticleByInput(String inputValue) {
        List<ArticleInputVo> list = baseMapper.getArticleByInput(inputValue);
        if (list != null && list.size() > 0) {
            return ResponseResult.okResult(list);
        } else {
            return ResponseResult.okResult(Collections.emptyList());
        }
    }

    @Override
    public ResponseResult articleDetailByTitle(String title) {
        LambdaQueryWrapper<Article> qw = new LambdaQueryWrapper<>();
        qw.eq(Article::getTitle, title);
        Article article = getOne(qw);
        if (article == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.INPUT_ERROR);
        }
        LambdaQueryWrapper<ArticleTag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ArticleTag::getArticleId, article.getId());
        // 根据文章id查询文章标签集合
        List<ArticleTag> list = articleTagService.list(queryWrapper);
        // 得到标签id的集合
        List<Long> tags = list.stream().map(ArticleTag::getTagId)
                .collect(Collectors.toList());
        // 封装Vo返回
        ArticleByIdVo articleByIdVo = new ArticleByIdVo();
        BeanUtils.copyProperties(article, articleByIdVo);
        articleByIdVo.setTags(tags);
        return ResponseResult.okResult(articleByIdVo);
    }

    private final ExecutorService executorService = Executors.newFixedThreadPool(5);

    /*@Override
    public ResponseResult download(Long id) {
        Article article = getById(id);
        // 示例Markdown内容
        String markdownContent = article.getContent();
        Map<String, String> data = new HashMap<>();
        // 解析Markdown内容为HTML
        String htmlContent = parseMarkdownToHtml(markdownContent);
        data.put("title", article.getTitle());
        data.put("content", htmlContent);
        return ResponseResult.okResult(data);
    }*/

    @Override
    public CompletableFuture<ResponseResult> downloadAsync(Long id) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Article article = getById(id);
                String markdownContent = article.getContent();
                Map<String, String> data = new HashMap<>();
                String htmlContent = parseMarkdownToHtml(markdownContent);
                data.put("title", article.getTitle());
                data.put("content", htmlContent);
                return ResponseResult.okResult(data);
            } catch (Exception e) {
                throw new RuntimeException("下载失败", e);
            }
        }, executorService);
    }
    private String parseMarkdownToHtml(String markdownContent) {
        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdownContent);
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        return renderer.render(document);
    }


}
