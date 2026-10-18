package com.yuuu.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 相关文章推荐条目
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RelatedArticleVo implements Serializable {
    private Long id;
    private String title;
    private String categoryName;
    private Date createTime;
    private Long viewCount;
}
