package com.yuuu.controller;

import com.yuuu.domain.ResponseResult;
import com.yuuu.service.SubscribeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 邮件订阅管理（后台）
 */
@RestController
@RequestMapping("/content/subscribe")
public class SubscribeAdminController {

    @Autowired
    private SubscribeService subscribeService;

    @PreAuthorize("@ps.hasPermission('content:subscribe:list')")
    @GetMapping("/list")
    public ResponseResult list(Integer pageNum, Integer pageSize, String status) {
        return subscribeService.adminList(pageNum, pageSize, status);
    }

    @PreAuthorize("@ps.hasPermission('content:subscribe:remove')")
    @DeleteMapping("/{ids}")
    public ResponseResult remove(@PathVariable List<Long> ids) {
        return subscribeService.deleteByIds(ids);
    }
}
