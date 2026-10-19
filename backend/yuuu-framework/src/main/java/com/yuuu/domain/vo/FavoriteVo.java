package com.yuuu.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 收藏列表条目（附带文章信息）
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteVo implements Serializable {
    private Long id;
    private Long articleId;
    private String title;
    private String summary;
    private String thumbnail;
    private Long viewCount;
    private Date createTime;
}
