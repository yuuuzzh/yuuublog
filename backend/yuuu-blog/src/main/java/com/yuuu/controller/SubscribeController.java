package com.yuuu.controller;

import com.yuuu.domain.ResponseResult;
import com.yuuu.service.SubscribeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 邮件订阅（前台匿名接口）
 */
@RestController
@RequestMapping("/subscribe")
public class SubscribeController {

    @Autowired
    private SubscribeService subscribeService;

    /**
     * 订阅本站更新
     */
    @PostMapping
    public ResponseResult subscribe(@RequestParam String email) {
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
