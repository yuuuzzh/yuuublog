package com.yuuu.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 前台归档：按年月分组
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArchiveVo implements Serializable {
    /** 年月，格式 yyyy-MM */
    private String yearMonth;
    private Integer total;
    private List<ArchiveArticleVo> articles;
}
