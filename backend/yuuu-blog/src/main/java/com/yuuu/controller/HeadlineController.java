package com.yuuu.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yuuu.domain.ResponseResult;
import com.yuuu.domain.entity.Headline;
import com.yuuu.service.HeadlineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 首屏文案（匿名）：每次刷新从启用池随机取一条诗句 + 一张背景图
 */
@RestController
public class HeadlineController {

    @Autowired
    private HeadlineService headlineService;

    /**
     * 首屏随机文案：直接写 HttpServletResponse，显式控制 Content-Type。
     * （此前用 Object/String 混合返回，Spring 内容协商把 JSONP 响应标成 text/html，
     *  触发浏览器 nosniff 严格校验拒绝执行 script——这正是"诗句不生效"的根因）
     */
    @GetMapping("/headline/random")
    public void random(@RequestParam(value = "callback", required = false) String callback,
                       javax.servlet.http.HttpServletResponse response) throws java.io.IOException {
        String quote = randomOne(Headline.TYPE_QUOTE);
        String bg = randomOne(Headline.TYPE_BG);
        response.setCharacterEncoding("utf-8");
        if (callback != null && callback.matches("[\\w$.]{1,64}")) {
            // JSONP：回调名白名单校验，内容做字符串转义
            response.setContentType("application/javascript;charset=utf-8");
            response.getWriter().write(callback + "({\"quote\":\"" + jsEscape(quote)
                    + "\",\"bgImage\":\"" + jsEscape(bg) + "\"});");
            return;
        }
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write("{\"code\":200,\"msg\":\"操作成功\",\"data\":{\"quote\":\""
                + jsEscape(quote) + "\",\"bgImage\":\"" + jsEscape(bg) + "\"}}");
    }

    /** JSON 字符串转义（引号/反斜杠/换行/回车） */
    private String jsEscape(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\r", "\\r").replace("\n", "\\n");
    }

    private String randomOne(String type) {
        List<Headline> list = headlineService.list(
                new LambdaQueryWrapper<Headline>()
                        .eq(Headline::getType, type)
                        .eq(Headline::getStatus, "0"));
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(new Random().nextInt(list.size())).getContent();
    }
}
