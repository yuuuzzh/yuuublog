package com.yuuu.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 邮件订阅(Subscribe)表实体类
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("yuuu_subscribe")
public class Subscribe implements Serializable {

    /** 订阅中 */
    public static final String STATUS_ACTIVE = "0";
    /** 已退订 */
    public static final String STATUS_CANCEL = "1";

    @TableId(type = IdType.AUTO)
    private Long id;

    private String email;
    /** 退订令牌 */
    private String token;
    /** 状态（0订阅中 1已退订） */
    private String status;
    private String source;
    private Date createTime;
    private Date updateTime;
    private Integer delFlag;
}
