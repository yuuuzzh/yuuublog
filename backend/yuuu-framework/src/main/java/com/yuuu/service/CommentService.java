package com.yuuu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuuu.domain.ResponseResult;
import com.yuuu.domain.entity.Comment;

import java.util.List;


/**
 * 评论表(Comment)表服务接口
 *
 * @author makejava
 * @since 2023-03-04 17:15:52
 */
public interface CommentService extends IService<Comment> {

    ResponseResult getCommentList(String commentType, Long articleId, Integer pageNum, Integer pageSize);

    ResponseResult addComment(Comment comment);


    ResponseResult getLikes(Long id);

    /**
     * 后台评论列表（含文章标题、评论人，便于审核）
     */
    ResponseResult adminList(Integer pageNum, Integer pageSize, String status, String type, String keyword);

    /**
     * 评论审核：批量设置状态（0待审核 1通过 2屏蔽）
     */
    ResponseResult audit(List<Long> ids, String status);

    /**
     * 批量删除评论
     */
    ResponseResult deleteByIds(List<Long> ids);
}

