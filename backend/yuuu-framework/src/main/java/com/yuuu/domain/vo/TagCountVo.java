package com.yuuu.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 前台标签云：标签 + 关联文章数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagCountVo implements Serializable {
    private Long id;
    private String name;
    /** 该标签下已发布的文章数 */
    private Integer articleCount;
}
