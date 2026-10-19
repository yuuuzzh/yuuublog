package com.yuuu.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yuuu.domain.ResponseResult;
import com.yuuu.domain.entity.Article;
import com.yuuu.domain.entity.Category;
import com.yuuu.domain.entity.Comment;
import com.yuuu.domain.entity.Tag;
import com.yuuu.service.ArticleService;
import com.yuuu.service.CategoryService;
import com.yuuu.service.CommentService;
import com.yuuu.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 后台数据仪表盘（统计口径：MySQL 现有业务数据，不含访问日志）
 */
@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private TagService tagService;

    @Autowired
    private CommentService commentService;

    @GetMapping("/stats")
    public ResponseResult stats() {
        List<Article> articles = articleService.list(
                new LambdaQueryWrapper<Article>().eq(Article::getDelFlag, 0));

        long views = 0;
        long likes = 0;
        Map<String, Long> monthlyMap = new TreeMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        for (Article article : articles) {
            views += article.getViewCount() == null ? 0 : article.getViewCount();
            likes += article.getLikes() == null ? 0 : article.getLikes();
            if (article.getCreateTime() != null) {
                String ym = sdf.format(article.getCreateTime());
                monthlyMap.put(ym, monthlyMap.getOrDefault(ym, 0L) + 1);
            }
        }

        // 总览
        Map<String, Object> overview = new LinkedHashMap<>();
        overview.put("articleCount", articles.size());
        overview.put("categoryCount", categoryService.count(new LambdaQueryWrapper<Category>().eq(Category::getDelFlag, 0)));
        overview.put("tagCount", tagService.count(new LambdaQueryWrapper<Tag>().eq(Tag::getDelFlag, 0)));
        overview.put("commentCount", commentService.count(new LambdaQueryWrapper<Comment>()));
        overview.put("viewCount", views);
        overview.put("likeCount", likes);

        // 分类分布
        List<Category> categories = categoryService.list(
                new LambdaQueryWrapper<Category>().eq(Category::getDelFlag, 0));
        List<Map<String, Object>> categoryStats = new ArrayList<>();
        for (Category category : categories) {
            long cnt = articles.stream()
                    .filter(a -> category.getId().equals(a.getCategoryId()))
                    .count();
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("name", category.getName());
            item.put("count", cnt);
            categoryStats.add(item);
        }
        categoryStats.sort(Comparator.comparingLong(o -> -((Long) o.get("count"))));

        // 热门文章 TOP10
        List<Article> top = new ArrayList<>(articles);
        top.sort(Comparator.comparingLong(o -> -(o.getViewCount() == null ? 0L : o.getViewCount())));
        List<Map<String, Object>> topArticles = new ArrayList<>();
        for (int i = 0; i < Math.min(10, top.size()); i++) {
            Article a = top.get(i);
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", a.getId());
            item.put("title", a.getTitle());
            item.put("viewCount", a.getViewCount() == null ? 0L : a.getViewCount());
            topArticles.add(item);
        }

        // 按月发布量
        List<Map<String, Object>> monthly = new ArrayList<>();
        for (Map.Entry<String, Long> entry : monthlyMap.entrySet()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("month", entry.getKey());
            item.put("count", entry.getValue());
            monthly.add(item);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("overview", overview);
        data.put("categoryStats", categoryStats);
        data.put("topArticles", topArticles);
        data.put("monthly", monthly);
        return ResponseResult.okResult(data);
    }
}
