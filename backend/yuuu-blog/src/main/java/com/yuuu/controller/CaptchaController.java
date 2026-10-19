package com.yuuu.controller;

/**
 * @author: yuuu
 * Description: 验证码接口
 * Email: 864326354@qq.com
 * CreatedTime: 2023-06-01 14:33:14:33
 */

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.yuuu.constants.SystemConstants;
import com.yuuu.domain.ResponseResult;
import com.yuuu.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/captcha")
public class CaptchaController {

    @Autowired
    private RedisCache redisCache;

    private DefaultKaptcha producer;

    public CaptchaController(DefaultKaptcha producer) {
        this.producer = producer;
    }
    @GetMapping()
    public ResponseResult captcha(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 生成验证码
        String text = producer.createText();
        BufferedImage image = producer.createImage(text);
        // 每次生成分配一个令牌：验证码按「令牌」维度存储，只对本次获取者生效，
        // 避免多人同时取验证码时互相顶掉（原来直接用验证码文本做 key，谁先提交就消费掉别人的）
        String captchaKey = UUID.randomUUID().toString().replace("-", "");
        request.getSession().setAttribute("captcha", text);
        redisCache.setCacheObject(SystemConstants.CAPTCHA_KEY + captchaKey, text, 60, TimeUnit.SECONDS);
        // 生成验证码背景图的 base64 编码
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", bos);
        String base64Image = Base64.getEncoder().encodeToString(bos.toByteArray());
        // 只返回令牌和图片，不再下发验证码明文
        Map<String, Object> data = new HashMap<>();
        data.put("captchaKey", captchaKey);
        data.put("captchaImageBase64", base64Image);
        return ResponseResult.okResult(data);
    }
}
