package com.yuuu.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 后台评论列表条目（附带文章标题与评论人，便于审核）
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentAdminVo implements Serializable {
    private Long id;
    /** 评论类型（0文章评论 1友链评论） */
    private String type;
    /** 审核状态（0待审核 1通过 2屏蔽） */
    private String status;
    private Long articleId;
    private String articleTitle;
    private String content;
    private Long createBy;
    private String userName;
    private Date createTime;
    private Long likes;
}
