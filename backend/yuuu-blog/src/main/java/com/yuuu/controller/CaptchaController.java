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
    /**
     * 算术验证码：返回题目与令牌（答案存 Redis，60 秒有效）。
     * 前端把题目渲染成胶囊样式，比扭曲图片更美观且与整体风格统一。
     */
    @GetMapping("/math")
    public ResponseResult mathCaptcha() {
        java.util.Random random = new java.util.Random();
        int a = 10 + random.nextInt(60);
        int b = 5 + random.nextInt(40);
        boolean plus = random.nextBoolean();
        // 减法保证非负
        int left = plus ? a : Math.max(a, b);
        int right = plus ? b : Math.min(a, b);
        int result = plus ? left + right : left - right;

        String captchaKey = UUID.randomUUID().toString().replace("-", "");
        redisCache.setCacheObject(SystemConstants.CAPTCHA_KEY + captchaKey, String.valueOf(result), 60, TimeUnit.SECONDS);

        Map<String, Object> data = new HashMap<>();
        data.put("captchaKey", captchaKey);
        data.put("question", left + (plus ? " + " : " − ") + right + " = ?");
        return ResponseResult.okResult(data);
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
