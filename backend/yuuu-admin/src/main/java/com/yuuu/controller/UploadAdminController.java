package com.yuuu.controller;

import com.yuuu.domain.ResponseResult;
import com.yuuu.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 已上传图片管理（后台，对象存储文件列举与删除）
 */
@RestController
@RequestMapping("/content/upload")
public class UploadAdminController {

    @Autowired
    private UploadService uploadService;

    @PreAuthorize("@ps.hasPermission('content:upload:list')")
    @GetMapping("/list")
    public ResponseResult list(@RequestParam(required = false) String marker,
                               @RequestParam(required = false) Integer limit) {
        return uploadService.listUploaded(marker, limit);
    }

    @PreAuthorize("@ps.hasPermission('content:upload:remove')")
    @DeleteMapping("/delete")
    public ResponseResult delete(@RequestParam String key) {
        return uploadService.deleteUploaded(key);
    }
}
