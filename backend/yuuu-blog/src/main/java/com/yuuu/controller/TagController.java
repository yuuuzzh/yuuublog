package com.yuuu.controller;

import com.yuuu.domain.ResponseResult;
import com.yuuu.domain.vo.TagCountVo;
import com.yuuu.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 标签（前台匿名接口）
 */
@RestController
@RequestMapping("/tag")
public class TagController {

    @Autowired
    private TagService tagService;

    /**
     * 标签云：标签 + 已发布文章数
     */
    @GetMapping("/listWithCount")
    public ResponseResult listWithCount() {
        List<TagCountVo> voList = tagService.listTagWithCount();
        return ResponseResult.okResult(voList);
    }
}
