package com.yuuu.notify;

import com.yuuu.domain.entity.Article;
import com.yuuu.domain.entity.Comment;
import com.yuuu.domain.entity.User;
import com.yuuu.service.ArticleService;
import com.yuuu.service.UserService;
import com.yuuu.utils.SendEmailUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.concurrent.CompletableFuture;

/**
 * 评论邮件通知：
 * - 根评论通知博主（blog 侧配置的邮箱）
 * - 回复他人评论时通知被回复者
 * 发送失败只记录日志，不影响评论发布。
 */
@Component
public class CommentNotifier {

    private static final Logger log = LoggerFactory.getLogger(CommentNotifier.class);

    /** 未指定回复对象时 toCommentUserId 的默认值 */
    private static final long NO_TARGET_USER = -1L;

    @Autowired(required = false)
    private SendEmailUtil sendEmailUtil;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private UserService userService;

    @Value("${site.url:https://www.yuuu.online}")
    private String siteUrl;

    @Value("${site.name:一蓑烟雨任平生}")
    private String siteName;

    /** 博主邮箱（与发件账号相同） */
    @Value("${mail.username:}")
    private String ownerEmail;

    public void notifyNewComment(Comment comment) {
        if (comment == null || sendEmailUtil == null || !StringUtils.hasText(ownerEmail)) {
            return;
        }
        CompletableFuture.runAsync(() -> {
            String articleTitle = resolveArticleTitle(comment);
            String link = siteUrl + "/DetailArticle?aid=" + comment.getArticleId();
            String content = comment.getContent() == null ? "" : comment.getContent();
            String html = sendEmailUtil.geneReplyMail(articleTitle, content, link);

            // 1. 回复他人：通知被回复者
            Long toUserId = comment.getToCommentUserId();
            if (toUserId != null && toUserId != NO_TARGET_USER) {
                User target = userService.getById(toUserId);
                if (target != null && StringUtils.hasText(target.getEmail())) {
                    send(target.getEmail(), "【" + siteName + "】有人回复了你的评论", html);
                }
            } else {
                // 2. 根评论：通知博主
                send(ownerEmail, "【" + siteName + "】文章有新评论：" + articleTitle, html);
            }
        });
    }

    private String resolveArticleTitle(Comment comment) {
        if (comment.getArticleId() != null) {
            Article article = articleService.getById(comment.getArticleId());
            if (article != null && StringUtils.hasText(article.getTitle())) {
                return article.getTitle();
            }
        }
        // 友链评论等没有文章的场景
        return siteName;
    }

    private void send(String to, String subject, String html) {
        try {
            sendEmailUtil.sendMessage(to, subject, html);
            log.info("评论通知邮件已发送：to={}", to);
        } catch (Exception e) {
            log.warn("评论通知邮件发送失败：to={}, err={}", to, e.getMessage());
        }
    }
}
