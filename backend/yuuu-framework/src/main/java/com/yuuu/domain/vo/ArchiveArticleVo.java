package com.yuuu.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 归档里的文章条目（只带列表必要字段）
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArchiveArticleVo implements Serializable {
    private Long id;
    private String title;
    private Date createTime;
}
