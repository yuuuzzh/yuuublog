package com.yuuu.service.impl;

import com.yuuu.constants.SystemConstants;
import com.yuuu.domain.ResponseResult;
import com.yuuu.domain.dto.UserLoginDto;
import com.yuuu.domain.entity.LoginUser;
import com.yuuu.domain.entity.User;
import com.yuuu.domain.vo.BlogUserLoginVo;
import com.yuuu.domain.vo.UserInfoVo;
import com.yuuu.enums.AppHttpCodeEnum;
import com.yuuu.exception.SystemException;
import com.yuuu.service.BlogLoginService;
import com.yuuu.utils.BeanCopyUtils;
import com.yuuu.utils.JwtUtil;
import com.yuuu.utils.RedisCache;
import com.yuuu.utils.RsaUtils;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @BelongsProject:YuuuBlog
 * @BelongsPackage:com.yuuu.service.impl
 * @Author:Yuuu。
 * @CreateTime:2023-03-04 09:50
 * @Description:
 * @Version:1.0
 */
@Service
@ConfigurationProperties(prefix = "rsa")
@Data
public class BlogLoginServiceImpl implements BlogLoginService {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private RedisCache redisCache;
    private String privateKey;

    @Override
    public ResponseResult login(UserLoginDto user) {
        String realPassword;
        try {
            realPassword = RsaUtils.decryptByPrivateKey(privateKey,user.getPassword());
        } catch (Exception e) {
            throw new SystemException(AppHttpCodeEnum.RSA_FAIL);
        }
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUserName(),realPassword);
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        //判断是否通过认证,密码校验上面的authenticate帮你完成了
        if (Objects.isNull(authenticate)){
            throw new RuntimeException("用户名或密码错误!");
        }
        //获取用户输入的验证码：按令牌定位本次验证码，校验后立即失效
        String captchaValue = user.getCaptcha();
        String captchaKey = user.getCaptchaKey();
        String cachedCaptcha = org.springframework.util.StringUtils.hasText(captchaKey)
                ? redisCache.getCacheObject(SystemConstants.CAPTCHA_KEY + captchaKey)
                : null;
        if (cachedCaptcha == null || captchaValue == null || !cachedCaptcha.equalsIgnoreCase(captchaValue.trim())){
            return ResponseResult.errorResult(602,"请再检查一下验证码哟~");
        } else {
            redisCache.deleteObject(SystemConstants.CAPTCHA_KEY + captchaKey);
        }
        //认证通过，用户名和密码都正确
        //获取userId，生成token
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        String userId = loginUser.getUser().getId().toString();
        String jwt = JwtUtil.createJWT(userId);
        //把用户信息存入redis
        redisCache.setCacheObject(SystemConstants.BLOG_LOGIN_KEY + userId,loginUser,1800, TimeUnit.SECONDS);
        //把token和userInfo封装返回
        //把user转换为userInfo
        UserInfoVo userInfoVo = BeanCopyUtils.copyBean(loginUser.getUser(), UserInfoVo.class);
        BlogUserLoginVo blogUserLoginVo = new BlogUserLoginVo(jwt, userInfoVo);
        return ResponseResult.okResult(blogUserLoginVo);
    }

    @Override
    public ResponseResult logout() {
        //获取token，解析userId
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        //获取userId
        Long id = loginUser.getUser().getId();
        //删除redis中的信息
        redisCache.deleteObject(SystemConstants.BLOG_LOGIN_KEY + id);
        return ResponseResult.okResult();
    }


}
