package com.yuuu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuuu.constants.SystemConstants;
import com.yuuu.service.ArticleService;
import com.yuuu.service.ArticleTagService;
import com.yuuu.service.TagService;
import com.yuuu.domain.ResponseResult;
import com.yuuu.domain.dto.TagDto;
import com.yuuu.domain.dto.TagListDto;
import com.yuuu.domain.entity.Article;
import com.yuuu.domain.entity.ArticleTag;
import com.yuuu.domain.entity.Tag;
import com.yuuu.domain.vo.PageVo;
import com.yuuu.domain.vo.TagCountVo;
import com.yuuu.domain.vo.TagVo;
import com.yuuu.enums.AppHttpCodeEnum;
import com.yuuu.mapper.TagMapper;
import com.yuuu.utils.BeanCopyUtils;
import com.yuuu.utils.BlogCacheManager;
import com.yuuu.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 标签(Tag)表服务实现类
 *
 * @author makejava
 * @since 2023-03-08 15:24:55
 */
@Service("tagService")
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ArticleTagService articleTagService;

    @Autowired
    private BlogCacheManager blogCacheManager;

    @Override
    public ResponseResult pageTagList(Integer pageNum, Integer pageSize, TagListDto tagListDto) {
        //分页
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(tagListDto.getName()),Tag::getName,tagListDto.getName())
                .like(StringUtils.hasText(tagListDto.getRemark()),Tag::getRemark,tagListDto.getRemark());
        Page<Tag> page = new Page(pageNum,pageSize);
        page(page,queryWrapper);
        List<Tag> tagList = page.getRecords();
        List<TagVo> tagListVos = BeanCopyUtils.copyBeanList(tagList, TagVo.class);
        //封装
        return ResponseResult.okResult(new PageVo(tagListVos,page.getTotal()));
    }

    @Override
    public ResponseResult addTag(TagListDto tagListDto) {
        Tag tag = new Tag();
        Long userId = SecurityUtils.getUserId();
        tag.setCreateBy(userId);
        tag.setName(tagListDto.getName());
        tag.setRemark(tagListDto.getRemark());
        tag.setCreateTime(new Date());
        tag.setUpdateTime(new Date());
        if (save(tag)){
            blogCacheManager.evictTag();
            return ResponseResult.okResult();
        }else {
            return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR);
        }
    }

    @Override
    public ResponseResult deleteTag(List<Long> ids) {
        removeByIds(ids);
        // 文章与标签的关联一并清理，避免标签云里残留已删标签
        articleTagService.remove(new LambdaQueryWrapper<ArticleTag>().in(ArticleTag::getTagId, ids));
        blogCacheManager.evictTag();
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult getTagById(Long id) {
        Tag tag = getById(id);
        if (Objects.nonNull(tag)){
            TagVo tagVo = BeanCopyUtils.copyBean(tag, TagVo.class);
            return ResponseResult.okResult(tagVo);
        }else{
            return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR);
        }
    }

    @Override
    public ResponseResult updateTag(TagDto tagDto) {
        Long userId = SecurityUtils.getUserId();
        Tag tag = new Tag();
        tag.setId(tagDto.getId());
        tag.setName(tagDto.getName());
        tag.setRemark(tagDto.getRemark());
        tag.setUpdateTime(new Date());
        tag.setUpdateBy(userId);
        boolean b = updateById(tag);
        if (b){
            blogCacheManager.evictTag();
            return ResponseResult.okResult();
        }else{
            return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR);
        }
    }

    @Override
    public ResponseResult listAllTag() {
        return ResponseResult.okResult(BeanCopyUtils.copyBeanList(list(),TagVo.class));
    }

    @Override
    public List<TagCountVo> listTagWithCount() {
        // 标签云访问频繁，缓存；标签/文章变更时由失效逻辑清理
        List<TagCountVo> cache = blogCacheManager.get(SystemConstants.TAG_LIST_KEY, List.class);
        if (cache != null) {
            return cache;
        }
        List<Tag> tags = list();
        // 只统计已发布文章，按标签分组
        List<Article> articles = articleService.list(
                new LambdaQueryWrapper<Article>().eq(Article::getStatus, SystemConstants.CATEGORY_STATUS_NORMAL));
        java.util.Set<Long> publishedIds = articles.stream().map(Article::getId).collect(java.util.stream.Collectors.toSet());
        List<ArticleTag> articleTags = articleTagService.list();
        java.util.Map<Long, Long> countMap = articleTags.stream()
                .filter(at -> publishedIds.contains(at.getArticleId()))
                .collect(java.util.stream.Collectors.groupingBy(ArticleTag::getTagId, java.util.stream.Collectors.counting()));

        List<TagCountVo> vos = tags.stream()
                .map(tag -> {
                    TagCountVo vo = new TagCountVo();
                    vo.setId(tag.getId());
                    vo.setName(tag.getName());
                    vo.setArticleCount(countMap.getOrDefault(tag.getId(), 0L).intValue());
                    return vo;
                })
                .filter(vo -> vo.getArticleCount() > 0)
                .collect(java.util.stream.Collectors.toList());
        blogCacheManager.set(SystemConstants.TAG_LIST_KEY, vos, SystemConstants.CACHE_TTL_LONG);
        return vos;
    }
}

