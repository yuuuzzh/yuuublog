package com.yuuu.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 首屏文案池：诗句 / 背景图，前台每次刷新随机取一条
 */
@Data
@TableName("yuuu_headline")
public class Headline {

    /** 类型：诗句 */
    public static final String TYPE_QUOTE = "1";
    /** 类型：背景图 */
    public static final String TYPE_BG = "2";

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 类型（1诗句 2背景图） */
    private String type;

    /** 诗句文本或图片URL */
    private String content;

    /** 状态（0启用 1停用） */
    private String status;

    private Date createTime;

    private Integer delFlag;
}
