package com.yuuu.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yuuu.constants.SystemConstants;
import com.yuuu.domain.entity.Article;
import com.yuuu.service.ArticleService;
import com.yuuu.utils.BlogCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 站点 SEO：sitemap.xml / RSS 订阅源（匿名访问，结果缓存，文章变更时失效）
 */
@RestController
public class SiteController {

    /** sitemap 最多收录的文章数 */
    private static final int SITEMAP_MAX = 500;
    /** RSS 输出的最近文章数 */
    private static final int RSS_MAX = 20;

    @Value("${site.url:https://www.yuuu.online}")
    private String siteUrl;

    @Value("${site.name:一蓑烟雨任平生}")
    private String siteName;

    @Value("${site.description:记录生活与技术的个人博客}")
    private String siteDescription;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private BlogCacheManager blogCacheManager;

    /**
     * sitemap.xml
     */
    @GetMapping(value = "/sitemap.xml", produces = "application/xml;charset=UTF-8")
    public String sitemap() {
        String cache = blogCacheManager.get(SystemConstants.SITEMAP_KEY, String.class);
        if (cache != null) {
            return cache;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");
        // 首页
        appendUrl(sb, siteUrl + "/Home", "1.0", null);
        // 固定页
        appendUrl(sb, siteUrl + "/Share?classId=0", "0.6", null);
        appendUrl(sb, siteUrl + "/About", "0.6", null);
        appendUrl(sb, siteUrl + "/FriendsLink", "0.4", null);
        appendUrl(sb, siteUrl + "/WordBoard", "0.4", null);
        // 文章
        List<Article> articles = publishedArticles(SITEMAP_MAX);
        for (Article article : articles) {
            appendUrl(sb, articleUrl(article.getId()), "0.8", article.getUpdateTime() == null ? article.getCreateTime() : article.getUpdateTime());
        }
        sb.append("</urlset>");
        String xml = sb.toString();
        blogCacheManager.set(SystemConstants.SITEMAP_KEY, xml, SystemConstants.CACHE_TTL_LONG);
        return xml;
    }

    /**
     * RSS 2.0 订阅源
     */
    @GetMapping(value = {"/rss.xml", "/feed"}, produces = "application/rss+xml;charset=UTF-8")
    public String rss() {
        String cache = blogCacheManager.get(SystemConstants.RSS_KEY, String.class);
        if (cache != null) {
            return cache;
        }
        SimpleDateFormat rfc822 = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
        rfc822.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));

        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<rss version=\"2.0\"><channel>\n");
        sb.append("<title>").append(escapeXml(siteName)).append("</title>\n");
        sb.append("<link>").append(escapeXml(siteUrl)).append("</link>\n");
        sb.append("<description>").append(escapeXml(siteDescription)).append("</description>\n");
        sb.append("<language>zh-CN</language>\n");

        for (Article article : publishedArticles(RSS_MAX)) {
            sb.append("<item>\n");
            sb.append("<title>").append(escapeXml(article.getTitle())).append("</title>\n");
            sb.append("<link>").append(escapeXml(articleUrl(article.getId()))).append("</link>\n");
            sb.append("<guid>").append(escapeXml(articleUrl(article.getId()))).append("</guid>\n");
            if (article.getCreateTime() != null) {
                sb.append("<pubDate>").append(rfc822.format(article.getCreateTime())).append("</pubDate>\n");
            }
            if (article.getSummary() != null) {
                sb.append("<description>").append(escapeXml(article.getSummary())).append("</description>\n");
            }
            sb.append("</item>\n");
        }
        sb.append("</channel></rss>");
        String xml = sb.toString();
        blogCacheManager.set(SystemConstants.RSS_KEY, xml, SystemConstants.CACHE_TTL_LONG);
        return xml;
    }

    /**
     * robots.txt
     */
    @GetMapping(value = "/robots.txt", produces = "text/plain;charset=UTF-8")
    public String robots() {
        return "User-agent: *\n"
                + "Allow: /\n"
                + "Disallow: /api/\n"
                + "\n"
                + "Sitemap: " + siteUrl + "/sitemap.xml\n";
    }

    /**
     * 已发布文章（按创建时间倒序）
     */
    private List<Article> publishedArticles(int limit) {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Article::getStatus, SystemConstants.CATEGORY_STATUS_NORMAL);
        wrapper.orderByDesc(Article::getCreateTime);
        wrapper.last("limit " + limit);
        return articleService.list(wrapper);
    }

    private String articleUrl(Long id) {
        return siteUrl + "/DetailArticle?aid=" + id;
    }

    private void appendUrl(StringBuilder sb, String loc, String priority, Date lastMod) {
        sb.append("<url>\n");
        sb.append("<loc>").append(escapeXml(loc)).append("</loc>\n");
        if (lastMod != null) {
            sb.append("<lastmod>").append(new SimpleDateFormat("yyyy-MM-dd").format(lastMod)).append("</lastmod>\n");
        }
        sb.append("<changefreq>weekly</changefreq>\n");
        sb.append("<priority>").append(priority).append("</priority>\n");
        sb.append("</url>\n");
    }

    /**
     * XML 转义，避免标题/摘要里的特殊字符破坏 XML 结构
     */
    private String escapeXml(String value) {
        if (value == null) {
            return "";
        }
        StringBuilder out = new StringBuilder(value.length());
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '&':
                    out.append("&amp;");
                    break;
                case '<':
                    out.append("&lt;");
                    break;
                case '>':
                    out.append("&gt;");
                    break;
                case '"':
                    out.append("&quot;");
                    break;
                case '\'':
                    out.append("&apos;");
                    break;
                default:
                    out.append(c);
            }
        }
        return out.toString();
    }
}
