package com.yuuu.utils;

import cn.hutool.core.map.MapUtil;
import cn.hutool.extra.template.Template;
import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;
import com.yuuu.enums.AppHttpCodeEnum;
import com.yuuu.exception.SystemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Component
public class SendEmailUtil {

    private final JavaMailSender mailSender;
    private static TemplateEngine smsEngine;
    private static TemplateEngine replyEngine;
    private static final Random RANDOM = new Random();

    private static SmsMailType[] smsMailTypes;
    private static ReplyMailType[] replyMailTypes;

    // 后台(admin)模块没有 mail 配置，这里给默认值兜底，避免启动时注入失败
    @Value("${mail.username:}")
    private String username;

    @Autowired
    public SendEmailUtil(JavaMailSender mailSender,
                         @Value("${mail.smsTemplateDir:templates/mail/sms}") String smsTemplateDir,
                         @Value("${mail.replyTemplateDir:templates/mail/reply}") String replyTemplateDir) {
        this.mailSender = mailSender;
        smsEngine = TemplateUtil.createEngine(new TemplateConfig(smsTemplateDir, TemplateConfig.ResourceMode.CLASSPATH));
        replyEngine = TemplateUtil.createEngine(new TemplateConfig(replyTemplateDir, TemplateConfig.ResourceMode.CLASSPATH));
    }

    @PostConstruct
    private void init() {
        smsMailTypes = SmsMailType.values();
        replyMailTypes = ReplyMailType.values();
    }

    public void sendMessage(String toEmail, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(username);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(message);
        } catch (Exception e) {
            throw new SystemException(AppHttpCodeEnum.EMAIL_ERROR);
        }
    }

    public String geneSmsMail(String code) {
        String templateName = getRandomTemplate(smsMailTypes);
        Template template = smsEngine.getTemplate(templateName);
        HashMap<String, String> map = MapUtil.of("verificationCode", code);
        return template.render(map);
    }

    public String geneReplyMail(String title, String content, String url) {
        String templateName = getRandomTemplate(replyMailTypes);
        Template template = replyEngine.getTemplate(templateName);
        Map<Object, Object> map = MapUtil.builder()
                .put("articleTitle", title)
                .put("replyContent", content)
                .put("articleLink", url)
                .map();
        return template.render(map);
    }

    private <T extends MailTemplateType> String getRandomTemplate(T[] templates) {
        return templates[RANDOM.nextInt(templates.length)].getFileName();
    }

    public interface MailTemplateType {
        String getFileName();
    }

    public enum SmsMailType implements MailTemplateType {
        TEMPLATE0("CuteSms.html"),
        TEMPLATE1("RetroSms.html"),
        TEMPLATE2("SimpleSms.html"),
        TEMPLATE3("TechnologySms.html");

        private final String fileName;

        SmsMailType(String fileName) {
            this.fileName = fileName;
        }

        @Override
        public String getFileName() {
            return fileName;
        }
    }

    public enum ReplyMailType implements MailTemplateType {
        TEMPLATE0("CuteReply.html"),
        TEMPLATE1("RetroReply.html"),
        TEMPLATE2("SimpleReply.html"),
        TEMPLATE3("TechnologyReply.html");

        private final String fileName;

        ReplyMailType(String fileName) {
            this.fileName = fileName;
        }

        @Override
        public String getFileName() {
            return fileName;
        }
    }
}
