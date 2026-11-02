package com.yuuu.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yuuu.domain.ResponseResult;
import com.yuuu.domain.entity.Headline;
import com.yuuu.service.HeadlineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/headline/random")
    public ResponseResult random() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("quote", randomOne(Headline.TYPE_QUOTE));
        data.put("bgImage", randomOne(Headline.TYPE_BG));
        // 池为空时返回 null，前端使用默认值兜底
        return ResponseResult.okResult(data);
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
