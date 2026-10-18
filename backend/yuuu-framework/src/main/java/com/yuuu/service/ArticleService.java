package com.yuuu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuuu.domain.dto.ArticleDto;
import com.yuuu.domain.dto.ArticleListDto;
import com.yuuu.domain.entity.Article;
import com.yuuu.domain.ResponseResult;
import com.yuuu.domain.vo.ArticleByIdVo;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;

import java.util.concurrent.CompletableFuture;

/**
 * @BelongsProject:YuuuBlog
 * @BelongsPackage:com.yuuu.service
 * @Author:Yuuu。
 * @CreateTime:2023-03-02 14:46
 * @Description:
 * @Version:1.0
 */
public interface ArticleService extends IService<Article> {
    ResponseResult hotArticleList();

    ResponseResult articleList(Integer pageNum, Integer pageSize, Long categoryId, Long tagId);

    /**
     * 前台归档：按年月聚合已发布文章
     */
    ResponseResult archives();

    /**
     * 相关文章推荐：同分类优先，不足时用热门补齐
     */
    ResponseResult relatedArticles(Long articleId, Integer size);

    /**
     * 全文搜索：标题或正文包含关键词的已发布文章
     */
    ResponseResult searchArticles(String keyword, Integer pageNum, Integer pageSize);

    ResponseResult articleDetailById(Long id);

    ResponseResult updateViewCount(Long articleId);

    ResponseResult add(ArticleDto articleDto);

    ResponseResult articleListAdmin(Integer pageNum, Integer pageSize, ArticleListDto articleListDto);

    ResponseResult getArticleById(Long id);

    ResponseResult updateArticle(ArticleByIdVo article);

    ResponseResult deleteArticle(Long id);

    ResponseResult getArticleByInput(String inputValue);

    ResponseResult articleDetailByTitle(String title);

    CompletableFuture<ResponseResult> downloadAsync(Long id);
}
