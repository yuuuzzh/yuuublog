package com.yuuu.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: yuuu
 * Description:
 * Email: 864326354@qq.com
 * CreatedTime: 2023-06-02 15:40:15:40
 */

@Data
public class UserLoginDto {

    //用户名
    private String userName;
    //密码
    private String password;
    //验证码
    private String captcha;
    //验证码令牌（获取验证码时下发，用于定位本次验证码）
    private String captchaKey;
}
