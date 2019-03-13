package com.crazychat.user.service;

import com.crazychat.user.dao.UserProfileDao;
import com.crazychat.user.pojo.UserProfile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {
    @Resource
    private UserProfileDao userProfileDao;

    @Resource
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Resource
    private JavaMailSenderImpl javaMailSender;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.mail.username}")
    private String email_from;

    /**
     * 登录验证
     *
     * @param email
     * @param password
     * @return
     */
    public UserProfile login(String email, String password) {
        // 根据邮箱查询
        UserProfile user = userProfileDao.findByEmail(email);
        // 验证密码
        if (null != user && bCryptPasswordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        return null;
    }

    /**
     * 发送邮箱验证码
     * @param email
     * @throws MessagingException
     */
    public void sendEmail(String email) throws MessagingException {
        // 创建一个复杂的邮件
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        // 参数一：MimeMessage
        // 参数二：是否发送文件
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false);
        // 生成验证码
        int i1 = (int) (Math.random() * 10);
        int i2 = (int) (Math.random() * 10);
        char c1 = (char) ((int) (Math.random() * 25 + 97));  // 97  122   0-1  0 + 97
        char c2 = (char) ((int) (Math.random() * 25 + 97));
        String code = String.valueOf(i1) + i2 + c1 + c2;
        // 存入缓存，15分钟超时
        redisTemplate.opsForValue().set(email + "_confirm_code", code, 15, TimeUnit.MINUTES);
        // 设置邮件信息
        helper.setSubject("CrazyChat注册验证码");
        String msg = "<h1>CrazyChat</h1>" +
                "<b>CrazyChat注册验证码为：<span style='color: #f00;'>" + code +
                "</span>，请在15分钟之内填写</b>";
        helper.setText(msg, true);         // 参数二：允许html
        helper.setTo(email);
        helper.setFrom(email_from);
        // 发送
        javaMailSender.send(mimeMessage);
    }
}
