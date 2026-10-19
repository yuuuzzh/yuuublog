package com.yuuu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuuu.domain.ResponseResult;
import com.yuuu.domain.entity.Article;
import com.yuuu.domain.entity.Subscribe;
import com.yuuu.domain.vo.PageVo;
import com.yuuu.enums.AppHttpCodeEnum;
import com.yuuu.mapper.SubscribeMapper;
import com.yuuu.service.SubscribeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.mail.internet.MimeMessage;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * 邮件订阅(Subscribe)表服务实现类
 */
@Service("subscribeService")
public class SubscribeServiceImpl extends ServiceImpl<SubscribeMapper, Subscribe> implements SubscribeService {

    private static final Logger log = LoggerFactory.getLogger(SubscribeServiceImpl.class);

    private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    @Value("${site.url:https://www.yuuu.online}")
    private String siteUrl;

    @Value("${site.name:一蓑烟雨任平生}")
    private String siteName;

    /** 后台模块可能没有配置邮件账号，这里允许为空（无配置时跳过推送） */
    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${mail.username:}")
    private String mailFrom;

    @Override
    public ResponseResult subscribe(String email, String source) {
        if (!StringUtils.hasText(email) || !email.trim().matches(EMAIL_REGEX)) {
            return ResponseResult.errorResult(AppHttpCodeEnum.INPUT_ERROR.getCode(), "邮箱格式不正确");
        }
        String mail = email.trim().toLowerCase();
        Subscribe exist = getOne(new LambdaQueryWrapper<Subscribe>().eq(Subscribe::getEmail, mail));
        if (exist != null) {
            if (Subscribe.STATUS_ACTIVE.equals(exist.getStatus())) {
                return ResponseResult.errorResult(AppHttpCodeEnum.INPUT_ERROR.getCode(), "这个邮箱已经订阅过啦");
            }
            // 之前退订过：重新激活
            exist.setStatus(Subscribe.STATUS_ACTIVE);
            exist.setUpdateTime(new Date());
            updateById(exist);
            return ResponseResult.okResult("订阅成功");
        }
        Subscribe sub = new Subscribe();
        sub.setEmail(mail);
        sub.setToken(UUID.randomUUID().toString().replace("-", ""));
        sub.setStatus(Subscribe.STATUS_ACTIVE);
        sub.setSource(StringUtils.hasText(source) ? source : "web");
        sub.setCreateTime(new Date());
        sub.setUpdateTime(new Date());
        sub.setDelFlag(0);
        save(sub);
        return ResponseResult.okResult("订阅成功");
    }

    @Override
    public ResponseResult unsubscribe(String token) {
        if (!StringUtils.hasText(token)) {
            return ResponseResult.errorResult(AppHttpCodeEnum.INPUT_ERROR);
        }
        Subscribe sub = getOne(new LambdaQueryWrapper<Subscribe>().eq(Subscribe::getToken, token));
        if (sub == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.INPUT_ERROR.getCode(), "退订链接无效");
        }
        sub.setStatus(Subscribe.STATUS_CANCEL);
        sub.setUpdateTime(new Date());
        updateById(sub);
        return ResponseResult.okResult("已退订");
    }

    @Override
    public ResponseResult adminList(Integer pageNum, Integer pageSize, String status) {
        LambdaQueryWrapper<Subscribe> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(status)) {
            wrapper.eq(Subscribe::getStatus, status);
        }
        wrapper.orderByDesc(Subscribe::getCreateTime);
        Page<Subscribe> page = new Page<>(
                (pageNum == null || pageNum < 1) ? 1 : pageNum,
                (pageSize == null || pageSize < 1) ? 10 : pageSize);
        page(page, wrapper);
        return ResponseResult.okResult(new PageVo(page.getRecords(), page.getTotal()));
    }

    @Override
    public ResponseResult deleteByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return ResponseResult.errorResult(AppHttpCodeEnum.INPUT_ERROR);
        }
        removeByIds(ids);
        return ResponseResult.okResult();
    }

    @Override
    public void notifyNewArticle(Article article) {
        if (article == null || mailSender == null || !StringUtils.hasText(mailFrom)) {
            log.info("未配置邮件账号，跳过新文章订阅推送：articleId={}", article == null ? null : article.getId());
            return;
        }
        // 异步发送：邮件失败或耗时都不能影响文章发布
        CompletableFuture.runAsync(() -> {
            List<Subscribe> list = list(new LambdaQueryWrapper<Subscribe>()
                    .eq(Subscribe::getStatus, Subscribe.STATUS_ACTIVE));
            if (list.isEmpty()) {
                return;
            }
            String url = siteUrl + "/DetailArticle?aid=" + article.getId();
            String subject = "【" + siteName + "】新文章：" + article.getTitle();
            int ok = 0;
            for (Subscribe sub : list) {
                try {
                    String unsubUrl = siteUrl + "/api/subscribe/unsubscribe?token=" + sub.getToken();
                    String html = buildMailHtml(article, url, unsubUrl);
                    MimeMessage message = mailSender.createMimeMessage();
                    MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                    helper.setFrom(mailFrom);
                    helper.setTo(sub.getEmail());
                    helper.setSubject(subject);
                    helper.setText(html, true);
                    mailSender.send(message);
                    ok++;
                } catch (Exception e) {
                    log.warn("新文章邮件发送失败：to={}, articleId={}, err={}", sub.getEmail(), article.getId(), e.getMessage());
                }
            }
            log.info("新文章订阅推送完成：articleId={}, 成功 {}/{}", article.getId(), ok, list.size());
        });
    }

    private String buildMailHtml(Article article, String url, String unsubUrl) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div style=\"font-family:-apple-system,'PingFang SC',sans-serif;max-width:600px;margin:auto\">");
        sb.append("<h2 style=\"color:#333\">").append(escapeHtml(article.getTitle())).append("</h2>");
        if (StringUtils.hasText(article.getSummary())) {
            sb.append("<p style=\"color:#666;line-height:1.8\">").append(escapeHtml(article.getSummary())).append("</p>");
        }
        sb.append("<p><a href=\"").append(url)
                .append("\" style=\"display:inline-block;padding:10px 18px;background:#4F8CFF;color:#fff;border-radius:6px;text-decoration:none\">阅读全文</a></p>");
        sb.append("<hr style=\"border:none;border-top:1px solid #eee;margin:24px 0\">");
        sb.append("<p style=\"font-size:12px;color:#999\">你收到这封邮件是因为订阅了本站更新。<a href=\"")
                .append(unsubUrl).append("\">退订</a></p>");
        sb.append("</div>");
        return sb.toString();
    }

    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
