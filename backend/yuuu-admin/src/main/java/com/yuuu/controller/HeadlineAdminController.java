package com.yuuu.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yuuu.domain.ResponseResult;
import com.yuuu.domain.entity.Headline;
import com.yuuu.service.HeadlineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * 首屏文案池管理（后台）
 */
@RestController
@RequestMapping("/content/headline")
public class HeadlineAdminController {

    @Autowired
    private HeadlineService headlineService;

    @PreAuthorize("@ps.hasPermission('content:headline:list')")
    @GetMapping("/list")
    public ResponseResult list(@RequestParam(required = false) String type) {
        LambdaQueryWrapper<Headline> wrapper = new LambdaQueryWrapper<>();
        if (type != null && !type.trim().isEmpty()) {
            wrapper.eq(Headline::getType, type);
        }
        wrapper.orderByDesc(Headline::getId);
        return ResponseResult.okResult(headlineService.list(wrapper));
    }

    @PreAuthorize("@ps.hasPermission('content:headline:edit')")
    @PostMapping
    public ResponseResult add(@RequestBody Headline headline) {
        headline.setId(null);
        headline.setStatus(headline.getStatus() == null ? "0" : headline.getStatus());
        headline.setCreateTime(new Date());
        headlineService.save(headline);
        return ResponseResult.okResult();
    }

    @PreAuthorize("@ps.hasPermission('content:headline:edit')")
    @PutMapping
    public ResponseResult update(@RequestBody Headline headline) {
        headlineService.updateById(headline);
        return ResponseResult.okResult();
    }

    @PreAuthorize("@ps.hasPermission('content:headline:edit')")
    @PutMapping("/toggle/{id}")
    public ResponseResult toggle(@PathVariable("id") Long id) {
        Headline headline = headlineService.getById(id);
        if (headline == null) {
            return ResponseResult.errorResult(500, "记录不存在");
        }
        headline.setStatus("0".equals(headline.getStatus()) ? "1" : "0");
        headlineService.updateById(headline);
        return ResponseResult.okResult();
    }

    @PreAuthorize("@ps.hasPermission('content:headline:remove')")
    @DeleteMapping("/{id}")
    public ResponseResult delete(@PathVariable("id") Long id) {
        headlineService.removeById(id);
        return ResponseResult.okResult();
    }
}
