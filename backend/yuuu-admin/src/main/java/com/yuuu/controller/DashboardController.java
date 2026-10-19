package com.yuuu.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yuuu.domain.ResponseResult;
import com.yuuu.domain.entity.Article;
import com.yuuu.domain.entity.Category;
import com.yuuu.domain.entity.Comment;
import com.yuuu.domain.entity.LoginConfig;
import com.yuuu.domain.entity.Tag;
import com.yuuu.service.ArticleService;
import com.yuuu.service.CategoryService;
import com.yuuu.service.CommentService;
import com.yuuu.service.LoginConfigService;
import com.yuuu.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
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

    @Autowired
    private LoginConfigService loginConfigService;

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
        data.put("visit", visitStats());
        return ResponseResult.okResult(data);
    }

    /**
     * 访客统计：总量、今日、近 30 天趋势、系统分布
     * 数据源为 VisitorInterceptor 自动写入的 login_config（type=2 为访客）
     */
    private Map<String, Object> visitStats() {
        Map<String, Object> visit = new LinkedHashMap<>();

        long total = loginConfigService.count(
                new LambdaQueryWrapper<LoginConfig>().eq(LoginConfig::getType, LoginConfig.VISITOR));
        long today = loginConfigService.count(
                new LambdaQueryWrapper<LoginConfig>()
                        .eq(LoginConfig::getType, LoginConfig.VISITOR)
                        .ge(LoginConfig::getLoginTime, startOfToday()));

        // 近 30 天趋势（按天聚合，缺失的日期补 0）
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DAY_OF_MONTH, -29);
        Date trendStart = cal.getTime();

        List<LoginConfig> recent = loginConfigService.list(
                new LambdaQueryWrapper<LoginConfig>()
                        .eq(LoginConfig::getType, LoginConfig.VISITOR)
                        .ge(LoginConfig::getLoginTime, trendStart)
                        .select(LoginConfig::getLoginTime, LoginConfig::getOs));

        SimpleDateFormat dayFmt = new SimpleDateFormat("MM-dd");
        Map<String, Long> dayMap = new TreeMap<>();
        Map<String, Long> osMap = new TreeMap<>();
        // 先把 30 个日期都填成 0，避免图表断点
        Calendar cursor = Calendar.getInstance();
        cursor.setTime(trendStart);
        for (int i = 0; i < 30; i++) {
            dayMap.put(dayFmt.format(cursor.getTime()), 0L);
            cursor.add(Calendar.DAY_OF_MONTH, 1);
        }
        for (LoginConfig log : recent) {
            if (log.getLoginTime() != null) {
                String day = dayFmt.format(log.getLoginTime());
                dayMap.put(day, dayMap.getOrDefault(day, 0L) + 1);
            }
            String os = (log.getOs() == null || log.getOs().trim().isEmpty()) ? "未知" : log.getOs();
            osMap.put(os, osMap.getOrDefault(os, 0L) + 1);
        }

        List<Map<String, Object>> trend = new ArrayList<>();
        for (Map.Entry<String, Long> entry : dayMap.entrySet()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("day", entry.getKey());
            item.put("count", entry.getValue());
            trend.add(item);
        }
        List<Map<String, Object>> osList = new ArrayList<>();
        List<Map.Entry<String, Long>> osEntries = new ArrayList<>(osMap.entrySet());
        osEntries.sort((o1, o2) -> Long.compare(o2.getValue(), o1.getValue()));
        for (int i = 0; i < Math.min(6, osEntries.size()); i++) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("name", osEntries.get(i).getKey());
            item.put("count", osEntries.get(i).getValue());
            osList.add(item);
        }

        visit.put("total", total);
        visit.put("today", today);
        visit.put("trend", trend);
        visit.put("os", osList);
        return visit;
    }

    private Date startOfToday() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
}
