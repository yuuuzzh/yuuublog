package com.yuuu.domain.entity;

import java.util.Date;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("yuuu_about")
public class About implements Serializable {
    @TableId
    private Long id;
    //称呼/昵称
    private String name;
    //一句话头衔
    private String title;
    //头像地址
    private String avatar;
    //页面顶部封面图
    private String cover;
    //签名/座右铭
    private String summary;
    //个人介绍正文（Markdown）
    private String content;
    //技能标签，逗号分隔，支持「名称:熟练度」写法
    private String skills;
    //所在城市
    private String location;
    //邮箱
    private String email;
    //GitHub 主页
    private String github;
    //个人网站
    private String website;
    //QQ号（前台按 wpa 临时会话链接加好友）
    private String qq;
    //微信二维码图片地址（微信没有可跳转主页，用扫码添加）
    private String wechatQr;
    //微博主页
    private String weibo;
    //CSDN 主页
    private String csdn;
    //Gitee 主页
    private String gitee;
    //状态0:正常,1停用
    private String status;
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
    //删除标志（0代表未删除，1代表已删除）
    private Integer delFlag;

}
