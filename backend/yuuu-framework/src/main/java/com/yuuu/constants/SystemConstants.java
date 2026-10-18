package com.yuuu.constants;

/**
 * @BelongsProject:YuuuBlog
 * @BelongsPackage:com.yuuu.constants
 * @Author:Yuuu。
 * @CreateTime:2023-03-02 16:57
 * @Description:
 * @Version:1.0
 */
public class SystemConstants {
        /**
         *  文章是草稿
         */
        public static final int ARTICLE_STATUS_DRAFT = 1;
        /**
         *  文章是正常发布状态
         */
        public static final int ARTICLE_STATUS_NORMAL = 0;
        public static final String CATEGORY_STATUS_NORMAL = "0";
        /**
         * 友链审核状态为通过
         */
        public static final String LINK_STATUS_NORMAL = "0";
        /**
         * 默认密码
         */
        public static final String DEFAULT_PASSWORD = "123456";
        /**
         * redis的key
         */
        public static final String BLOG_LOGIN_KEY = "blog:login:";

        /**
         * redis消息唯一的key
         */
        public static final String BLOG_CHAT_KEY = "chat:user:";
        /**
         * redis聊天限流的key
         */
        public static final String BLOG_CHAT_COUNTER = "chat:counter:";
        /**
         * 根评论id
         */
        public static final int COMMENT_ROOT_ID = -1;
        /**
         * 文章评论
         */
        public static final String ARTICLE_COMMENT = "0";
        /**
         * 友链评论
         */
        public static final String LINK_COMMENT = "1";
        /**
         * 域名地址
         */
        public static final String URL_PATH = "https://qiniu.yuuu.online/";


        /**
         * 文章访问量
         */
        public static final String VIEW_COUNT = "article:viewCount";

        /**
         * 后台登录
         */
        public static final String LOGIN_KEY = "login:";
        public static final String CAPTCHA_CODE_KEY = "captcha_codes:";

        /**
         * 验证码有效期（分钟）
         */
        public static final Integer CAPTCHA_EXPIRATION = 2;

        /**
         * 菜单类型
         */
        public static final String MENU = "C";
        /**
         * 按钮类型
         */
        public static final String BUTTON = "F";
        /**
         * 正常状态
         */
        public static final String STATUS_NORMAL = "0";
        /**
         * 管理员用户
         */
        public static final String ADMIN = "1";
        /**
         * 表示该用户为超级管理员
         */
        public static final String CLOSED_USERNAME="已注销用户";
        /**
         * 验证码prefix-key
         */
        public static final String CAPTCHA_KEY="captcha:";

        public static final String MAIL_KEY_0 = "mail:register:";
        public static final String MAIL_KEY_1 = "mail:upwd:";

        /* ==================== 前台热点缓存 key ==================== */
        /** 前台文章列表：article:list:{categoryId}:{pageNum}:{pageSize} */
        public static final String ARTICLE_LIST_KEY = "article:list:";
        /** 前台文章详情：article:detail:{id} */
        public static final String ARTICLE_DETAIL_KEY = "article:detail:";
        /** 前台热门文章 */
        public static final String HOT_ARTICLE_KEY = "article:hot";
        /** 前台分类列表 */
        public static final String CATEGORY_LIST_KEY = "category:list";
        /** 前台友链列表 */
        public static final String LINK_LIST_KEY = "link:list";
        /** 前台关于我 */
        public static final String ABOUT_KEY = "about:front";
        /** sitemap.xml 渲染结果 */
        public static final String SITEMAP_KEY = "site:sitemap";
        /** RSS 订阅源渲染结果 */
        public static final String RSS_KEY = "site:rss";
        /** 前台标签云 */
        public static final String TAG_LIST_KEY = "tag:listWithCount";
        /** 前台文章归档 */
        public static final String ARCHIVE_KEY = "article:archive";

        /** 缓存有效期（分钟）：变动较频繁的列表/详情 */
        public static final Integer CACHE_TTL_SHORT = 10;
        /** 缓存有效期（分钟）：配置型数据 */
        public static final Integer CACHE_TTL_LONG = 30;
}
