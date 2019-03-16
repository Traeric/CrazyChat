package com.crazychat.user.service;

import com.crazychat.common.utils.IdWorker;
import com.crazychat.user.dao.FriendDao;
import com.crazychat.user.dao.FriendGroupDao;
import com.crazychat.user.dao.UserProfileDao;
import com.crazychat.user.pojo.Friend;
import com.crazychat.user.pojo.UserProfile;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class UserService {
    @Resource
    private UserProfileDao userProfileDao;

    @Resource
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Resource
    private JavaMailSenderImpl javaMailSender;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource(name = "idWorker")
    private IdWorker idCreater;

    @Resource
    private FriendDao friendDao;

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
        // 生成6位随机验证码
        String code = RandomStringUtils.randomNumeric(4);
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

    /**
     * 用户注册
     * @param user
     * @param code
     */
    public void register(UserProfile user, String code) {
        // 获取验证码
        String confirmCode = (String) redisTemplate.opsForValue().get(user.getEmail() + "_confirm_code");
        if (code.equals(confirmCode)) {
            // 进行保存
            // 生成id
            user.setId(String.valueOf(idCreater.nextId()));
            // 密码加密
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            System.out.println(user);
            userProfileDao.save(user);   // 保存
        } else {
            String msg;
            if (confirmCode != null) {
                msg = "验证码错误";
            } else {
                msg = "验证码已过期";
            }
            throw new RuntimeException(msg);
        }
    }

    /**
     * 获取用户的好友列表
     * @param user_id
     * @return
     */
    public Map<String, Object> getFriendList(String user_id) {
        // 查询出用户的全部分组
        UserProfile user = userProfileDao.findById(user_id).orElse(null);
        if (null == user) {
            throw new RuntimeException("用户不存在");
        }
        // 准备返回的对象
        Map<String, Object> data = new HashMap<>();
        user.getFriendGroups().parallelStream().forEach((group) -> {
            // 获取当前分组下的所有好友
            List<Friend> friends = friendDao.findAllByFriendgroupId(group.getId());
            // 一个分组的所有好友
            List<Map<String, String>> list = new LinkedList<>();
            friends.parallelStream().forEach((friend) -> {
                Map<String, String> map = new HashMap<>();
                map.put("id", friend.getId());
                map.put("todo", friend.getTodo());
                // 查询好友头像
                String avatar = userProfileDao.getAvatar(friend.getFriendId());
                map.put("avatar", avatar);
                list.add(map);
            });
            data.put(group.getName(), list);
        });
        return data;
    }

    /**
     * 根据id获取用户名
     * @param userId
     * @return
     */
    public UserProfile getNameById(String userId) {
        return userProfileDao.findById(userId).orElse(null);
    }

    /**
     * 获取用户信息
     * @param userId
     * @return
     */
    public UserProfile getUserInfo(String userId) {
        return userProfileDao.findById(userId).orElse(null);
    }
}
