package com.yuuu.utils;

import com.yuuu.constants.SystemConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * 前台热点缓存管理：读写缓存 + 后台写库后的失效
 *
 * 失效放在 framework 层（前台与后台共用同一份 Service 实现），
 * 后台进程写库后直接删除 Redis 中的缓存 key，前台进程下一次读即回源。
 * 前提：前台(yuuu-blog)与后台(yuuu-admin)必须使用同一个 Redis database。
 *
 * 所有 Redis 操作都做降级：Redis 不可用时缓存视为未命中/忽略失效，直接走数据库，
 * 避免缓存故障放大成接口 500。
 */
@Component
public class BlogCacheManager {

    private static final Logger log = LoggerFactory.getLogger(BlogCacheManager.class);

    @Autowired
    private RedisCache redisCache;

    /**
     * 读缓存，类型不匹配时当作未命中（并清理脏数据），避免 Fastjson 反序列化类型漂移导致强转异常
     */
    public <T> T get(String key, Class<T> type) {
        try {
            Object cache = redisCache.getCacheObject(key);
            if (cache == null) {
                return null;
            }
            if (!type.isInstance(cache)) {
                log.warn("缓存类型不匹配，按未命中处理并清理：key={}, expect={}, actual={}",
                        key, type.getName(), cache.getClass().getName());
                redisCache.deleteObject(key);
                return null;
            }
            return type.cast(cache);
        } catch (Exception e) {
            log.warn("读取缓存失败，降级走数据库：key={}, err={}", key, e.getMessage());
            return null;
        }
    }

    /**
     * 写缓存，统一带 TTL，避免无过期时间的脏数据长期残留
     */
    public void set(String key, Object value, Integer minutes) {
        if (value == null || minutes == null) {
            return;
        }
        try {
            redisCache.setCacheObject(key, value, minutes, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.warn("写入缓存失败，忽略：key={}, err={}", key, e.getMessage());
        }
    }

    /**
     * 文章新增/修改/删除后失效：详情、热门榜、所有分页列表
     */
    public void evictArticle(Long articleId) {
        try {
            if (articleId != null) {
                redisCache.deleteObject(SystemConstants.ARTICLE_DETAIL_KEY + articleId);
            }
            redisCache.deleteObject(SystemConstants.HOT_ARTICLE_KEY);
            deleteByPrefix(SystemConstants.ARTICLE_LIST_KEY);
            // 标签云的文章数、归档分组都会随文章变化
            redisCache.deleteObject(SystemConstants.TAG_LIST_KEY);
            redisCache.deleteObject(SystemConstants.ARCHIVE_KEY);
            // sitemap / RSS 里包含文章链接，文章变化需同步失效
            evictSite();
        } catch (Exception e) {
            log.warn("失效文章缓存失败（数据以数据库为准）：articleId={}, err={}", articleId, e.getMessage());
        }
    }

    /**
     * sitemap / RSS 失效
     */
    public void evictSite() {
        try {
            redisCache.deleteObject(SystemConstants.SITEMAP_KEY);
            redisCache.deleteObject(SystemConstants.RSS_KEY);
        } catch (Exception e) {
            log.warn("失效 sitemap/RSS 缓存失败（数据以数据库为准）：err={}", e.getMessage());
        }
    }

    /**
     * 分类新增/修改/删除/改状态后失效：分类列表 + 文章列表（列表返回体含 categoryName）
     */
    public void evictCategory() {
        try {
            redisCache.deleteObject(SystemConstants.CATEGORY_LIST_KEY);
            deleteByPrefix(SystemConstants.ARTICLE_LIST_KEY);
        } catch (Exception e) {
            log.warn("失效分类缓存失败（数据以数据库为准）：err={}", e.getMessage());
        }
    }

    /**
     * 友链新增/修改/删除后失效
     */
    public void evictLink() {
        try {
            redisCache.deleteObject(SystemConstants.LINK_LIST_KEY);
        } catch (Exception e) {
            log.warn("失效友链缓存失败（数据以数据库为准）：err={}", e.getMessage());
        }
    }

    /**
     * 标签新增/修改/删除后失效：标签云（文章数会变）
     */
    public void evictTag() {
        try {
            redisCache.deleteObject(SystemConstants.TAG_LIST_KEY);
        } catch (Exception e) {
            log.warn("失效标签缓存失败（数据以数据库为准）：err={}", e.getMessage());
        }
    }

    /**
     * 关于我保存后失效
     */
    public void evictAbout() {
        try {
            redisCache.deleteObject(SystemConstants.ABOUT_KEY);
        } catch (Exception e) {
            log.warn("失效关于我缓存失败（数据以数据库为准）：err={}", e.getMessage());
        }
    }

    /**
     * 按前缀批量删除（文章分页 key 数量有限，可接受一次 KEYS 扫描）
     */
    private void deleteByPrefix(String prefix) {
        Collection<String> keys = redisCache.keys(prefix + "*");
        if (keys != null && !keys.isEmpty()) {
            redisCache.deleteObject(keys);
        }
    }
}
