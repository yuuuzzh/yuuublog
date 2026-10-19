package com.yuuu.controller;

import com.yuuu.constants.SystemConstants;
import com.yuuu.domain.ResponseResult;
import com.yuuu.domain.entity.Comment;
import com.yuuu.enums.AppHttpCodeEnum;
import com.yuuu.notify.CommentNotifier;
import com.yuuu.service.CommentService;
import com.yuuu.utils.RedisCache;
import com.yuuu.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

/**
 * @BelongsProject:YuuuBlog
 * @BelongsPackage:com.yuuu.controller
 * @Author:Yuuu。
 * @CreateTime:2023-03-04 18:35
 * @Description:
 * @Version:1.0
 */
@RestController
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentNotifier commentNotifier;

    @Autowired
    private RedisCache redisCache;

    /** 同一用户 60 秒内允许的评论条数 */
    private static final int COMMENT_LIMIT_PER_MINUTE = 3;

    @GetMapping("/commentList")
    public ResponseResult getCommentList(Long articleId, Integer pageNum, Integer pageSize){
        return commentService.getCommentList(SystemConstants.ARTICLE_COMMENT,articleId,pageNum,pageSize);
    }

    @PostMapping
    public ResponseResult addComment(@RequestBody Comment comment){
        // 同一用户 60 秒内最多 3 条，避免评论通知邮件被刷
        Long userId = SecurityUtils.getUserId();
        String rateKey = "comment:rate:" + userId;
        Integer used = redisCache.getCacheObject(rateKey);
        if (used != null && used.intValue() >= COMMENT_LIMIT_PER_MINUTE) {
            return ResponseResult.errorResult(AppHttpCodeEnum.OVER_LOCKING.getCode(), "评论太频繁了，歇一会儿再发吧");
        }
        redisCache.setCacheObject(rateKey, (used == null ? 1 : used.intValue() + 1), 60, TimeUnit.SECONDS);

        ResponseResult result = commentService.addComment(comment);
        // 评论发布成功后异步邮件通知（失败不影响评论结果）
        commentNotifier.notifyNewComment(comment);
        return result;
    }

    @GetMapping("/linkCommentList")
    public ResponseResult linkCommentList(Integer pageNum, Integer pageSize){
        return commentService.getCommentList(SystemConstants.LINK_COMMENT,null,pageNum,pageSize);
    }

    @GetMapping("/likes/{id}")
    public ResponseResult getLikes(@PathVariable("id") Long id){
        return commentService.getLikes(id);
    }
}
