package com.yuuu.controller;

import com.yuuu.domain.ResponseResult;
import com.yuuu.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 评论管理（后台）
 */
@RestController
@RequestMapping("/content/comment")
public class CommentAdminController {

    @Autowired
    private CommentService commentService;

    @PreAuthorize("@ps.hasPermission('content:comment:list')")
    @GetMapping("/list")
    public ResponseResult list(Integer pageNum, Integer pageSize, String status, String type, String keyword) {
        return commentService.adminList(pageNum, pageSize, status, type, keyword);
    }

    /**
     * 审核：body = { ids: [1,2], status: "1" }（0待审核 1通过 2屏蔽）
     */
    @PreAuthorize("@ps.hasPermission('content:comment:audit')")
    @PostMapping("/audit")
    public ResponseResult audit(@RequestBody Map<String, Object> body) {
        List<Long> ids = toLongList(body.get("ids"));
        String status = body.get("status") == null ? null : String.valueOf(body.get("status"));
        return commentService.audit(ids, status);
    }

    @PreAuthorize("@ps.hasPermission('content:comment:remove')")
    @DeleteMapping("/{ids}")
    public ResponseResult remove(@PathVariable List<Long> ids) {
        return commentService.deleteByIds(ids);
    }

    @SuppressWarnings("unchecked")
    private List<Long> toLongList(Object value) {
        if (!(value instanceof List)) {
            return java.util.Collections.emptyList();
        }
        List<Object> raw = (List<Object>) value;
        List<Long> ids = new java.util.ArrayList<>();
        for (Object item : raw) {
            if (item == null) {
                continue;
            }
            try {
                ids.add(Long.valueOf(String.valueOf(item)));
            } catch (NumberFormatException e) {
                // 忽略非法 id
            }
        }
        return ids;
    }
}
