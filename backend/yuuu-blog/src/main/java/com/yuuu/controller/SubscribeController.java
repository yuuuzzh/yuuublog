package com.yuuu.controller;

import com.yuuu.constants.SystemConstants;
import com.yuuu.domain.ResponseResult;
import com.yuuu.enums.AppHttpCodeEnum;
import com.yuuu.service.SubscribeService;
import com.yuuu.utils.IPUtil;
import com.yuuu.utils.RedisCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

/**
 * 邮件订阅（前台匿名接口）
 */
@RestController
@RequestMapping("/subscribe")
public class SubscribeController {

    private static final Logger log = LoggerFactory.getLogger(SubscribeController.class);

    /** 同一 IP 两次订阅的最小间隔（秒） */
    private static final int GAP_SECONDS = 60;
    /** 同一 IP 每天最多订阅次数 */
    private static final int DAY_LIMIT = 10;

    @Autowired
    private SubscribeService subscribeService;

    @Autowired
    private RedisCache redisCache;

    /**
     * 订阅本站更新：验证码 + 同 IP 限流，防止被刷
     */
    @PostMapping
    public ResponseResult subscribe(@RequestParam String email,
                                   @RequestParam(required = false) String captcha,
                                   HttpServletRequest request) {
        if (!org.springframework.util.StringUtils.hasText(captcha)) {
            return ResponseResult.errorResult(AppHttpCodeEnum.INPUT_ERROR.getCode(), "请输入验证码");
        }
        // 验证码一次性校验
        String cached = redisCache.getCacheObject(SystemConstants.CAPTCHA_KEY + captcha.trim());
        if (cached == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.INPUT_ERROR.getCode(), "验证码不正确或已过期");
        }
        redisCache.deleteObject(SystemConstants.CAPTCHA_KEY + captcha.trim());

        // 同 IP 限流（Redis 异常时拒绝，避免限流被绕过）
        String ip = IPUtil.getIpAddr(request);
        try {
            String gapKey = "subscribe:gap:" + ip;
            if (redisCache.getCacheObject(gapKey) != null) {
                return ResponseResult.errorResult(AppHttpCodeEnum.OVER_LOCKING.getCode(), "操作太频繁，请稍后再试");
            }
            String dayKey = "subscribe:day:" + ip;
            Integer used = redisCache.getCacheObject(dayKey);
            if (used != null && used.intValue() >= DAY_LIMIT) {
                return ResponseResult.errorResult(AppHttpCodeEnum.OVER_LOCKING.getCode(), "今日订阅次数已达上限");
            }
            redisCache.setCacheObject(gapKey, 1, GAP_SECONDS, TimeUnit.SECONDS);
            redisCache.setCacheObject(dayKey, (used == null ? 1 : used.intValue() + 1), 24, TimeUnit.HOURS);
        } catch (Exception e) {
            log.warn("订阅限流校验失败，本次拒绝：ip={}, err={}", ip, e.getMessage());
            return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR.getCode(), "服务繁忙，请稍后再试");
        }

        return subscribeService.subscribe(email, "web");
    }

    /**
     * 一键退订：直接返回提示页（邮件里点开即可）
     */
    @GetMapping(value = "/unsubscribe", produces = "text/html;charset=UTF-8")
    public String unsubscribe(@RequestParam(required = false) String token) {
        ResponseResult result = subscribeService.unsubscribe(token);
        boolean ok = result != null && Integer.valueOf(200).equals(result.getCode());
        String title = ok ? "退订成功" : "退订失败";
        String desc = ok ? "已取消订阅，不会再收到新文章通知。" : "链接无效或已过期，请回到站点重新操作。";
        return "<!DOCTYPE html><html lang=\"zh-CN\"><head><meta charset=\"UTF-8\">"
                + "<meta name=\"viewport\" content=\"width=device-width,initial-scale=1\">"
                + "<title>" + title + "</title></head>"
                + "<body style=\"font-family:-apple-system,'PingFang SC',sans-serif;display:flex;align-items:center;"
                + "justify-content:center;height:100vh;margin:0;background:#f6f8fb\">"
                + "<div style=\"text-align:center;padding:32px;background:#fff;border-radius:12px;box-shadow:0 8px 30px rgba(20,30,60,.12)\">"
                + "<h2 style=\"margin:0 0 12px;color:#333\">" + title + "</h2>"
                + "<p style=\"color:#666;margin:0 0 20px\">" + desc + "</p>"
                + "<a href=\"/\" style=\"color:#4F8CFF;text-decoration:none\">返回首页</a>"
                + "</div></body></html>";
    }
}
