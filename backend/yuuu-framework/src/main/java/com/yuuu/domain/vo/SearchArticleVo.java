package com.yuuu.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 搜索结果条目（含关键词上下文片段，高亮由前端渲染时处理，后端不产出 HTML）
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchArticleVo implements Serializable {
    private Long id;
    private String title;
    /** 关键词附近的正文片段（已去掉常见 Markdown 标记） */
    private String excerpt;
    private Long categoryId;
    private String categoryName;
    private Date createTime;
    private Long viewCount;
}
