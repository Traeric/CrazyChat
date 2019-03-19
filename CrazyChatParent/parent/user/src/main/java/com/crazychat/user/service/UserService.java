package com.crazychat.user.service;

import com.crazychat.common.utils.FileResource;
import com.crazychat.common.utils.IdWorker;
import com.crazychat.common.utils.MD5Utils;
import com.crazychat.user.client.ChatClient;
import com.crazychat.user.dao.FriendDao;
import com.crazychat.user.dao.FriendGroupDao;
import com.crazychat.user.dao.UserProfileDao;
import com.crazychat.user.pojo.Friend;
import com.crazychat.user.pojo.FriendGroup;
import com.crazychat.user.pojo.UserProfile;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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

    @Resource
    private ChatClient chatClient;

    @Resource
    private FriendGroupDao friendGroupDao;

    @Value("${auth-key}")
    private String token;

    @Value("${url-path}")
    private String hostPath;

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
    @Async
    public void sendEmail(String email) throws MessagingException {
        // 生成6位随机验证码
        String code = RandomStringUtils.randomNumeric(4);
        // 存入缓存，15分钟超时
        redisTemplate.opsForValue().set(email + "_confirm_code", code, 15, TimeUnit.MINUTES);
        String msg = "<h1>CrazyChat</h1>" +
                "<b>CrazyChat注册验证码为：<span style='color: #f00;'>" + code +
                "</span>，请在15分钟之内填写</b>";
        this.sendEmail("CrazyChat注册验证码", msg, email);
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
        // 通过id获取所有的分组
        List<FriendGroup> friendGroups = friendGroupDao.findAllByUserId(user.getId());
        friendGroups.parallelStream().forEach((group) -> {
            // 获取当前分组下的所有好友
            List<Friend> friends = friendDao.findAllByFriendgroupId(group.getId());
            // 一个分组的所有好友
            List<Map<String, String>> list = new LinkedList<>();
            friends.parallelStream().forEach((friend) -> {
                Map<String, String> map = new HashMap<>();
                map.put("id", friend.getFriendId());
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

    /**
     * 查询用户聊天记录
     * @param userId
     * @param friendId
     * @return
     */
    public List<Map<String, String>> getChatRecord(String userId, String friendId) {
        return chatClient.getChatRecord(userId, friendId);
    }

    /**
     * 获取用户分组
     * @param userId
     * @return
     */
    public List<Map<String, String>> getGroupList(String userId) {
        UserProfile user = userProfileDao.findById(userId).orElse(null);
        if (null == user) {
            throw new RuntimeException("用户不存在");
        }
        List<FriendGroup> groups = friendGroupDao.findAllByUserId(user.getId());
        List<Map<String, String>> data = new ArrayList<>();
        groups.parallelStream().forEach((group) -> {
            Map<String, String> map = new HashMap<>();
            map.put("id", group.getId());
            map.put("name", group.getName());
            data.add(map);
        });
        return data;
    }

    /**
     * 修改分组
     * @param friendId
     * @param oldGroup
     * @param newGroup
     */
    public void changeGroup(String friendId, String oldGroup, String newGroup) {
        // 查出原分组信息
        Friend friend = friendDao.findByFriendgroupIdAndFriendId(oldGroup, friendId);
        // 更新分组
        friend.setFriendgroupId(newGroup);
        friendDao.save(friend);
    }

    /**
     * 修改好友备注
     * @param userId
     * @param friendId
     */
    public void changeTodo(String userId, String friendId, String todo) {
        // 通过userId和friendId获取好友信息
        Friend friend = friendDao.findByUserIdAndFriendId(userId, friendId);
        friend.setTodo(todo);
        friendDao.save(friend);
    }

    /**
     * 搜搜索用户
     * @param userName
     * @return
     */
    public List<Map<String, String>> searchUser(String userName) {
        List<UserProfile> users = userProfileDao.findByNameContains(userName);
        List<Map<String, String>> data = new ArrayList<>();
        users.parallelStream().forEach((user) -> {
            Map<String, String> map = new HashMap<>();
            map.put("id", user.getId());
            map.put("name", user.getName());
            map.put("avatar", user.getAvatar());
            map.put("sign", user.getSign());
            data.add(map);
        });
        return data;
    }

    /**
     * 修改昵称
     * @param userId
     * @param nick
     */
    public void profileNick(String userId, String nick) {
        // 查询出用户
        UserProfile user = userProfileDao.findById(userId).orElse(null);
        if (null == user) {
            throw new RuntimeException("没有该用户");
        }
        // 修改昵称
        user.setName(nick);
        userProfileDao.save(user);
    }

    /**
     * 修改用户信息
     * @param userId
     * @param sign
     * @param gender
     * @param birth
     * @param describe
     */
    public void profileInfo(String userId, String sign, String gender, Date birth, String describe) {
        // 查询出用户
        UserProfile user = userProfileDao.findById(userId).orElse(null);
        if (null == user) {
            throw new RuntimeException("没有该用户");
        }
        // 设置信息
        user.setSign(sign);
        user.setGender(gender);
        user.setBirthday(birth);
        user.setUserdescribe(describe);
        // 保存用户
        userProfileDao.save(user);
    }

    /**
     * 新增分组
     * @param userId
     * @param groupName
     */
    public void addGroup(String userId, String groupName) {
        FriendGroup friendGroup = new FriendGroup();
        friendGroup.setId(String.valueOf(idCreater.nextId()));
        friendGroup.setName(groupName);
        friendGroup.setUserId(userId);
        friendGroupDao.save(friendGroup);
    }

    /**
     * 查询当前分组下是否还有好友
     * @param groupId
     * @return
     */
    public boolean groupIsEmpty(String groupId) {
        System.out.println(groupId);
        List<Friend> friends = friendDao.findAllByFriendgroupId(groupId);
        return friends.size() == 0;
    }

    /**
     * 删除分组
     * @param groupId
     */
    public void deleteGroup(String groupId) {
        friendGroupDao.deleteById(groupId);
    }

    /**
     * 修改邮箱
     * @param userId
     * @param newEmail
     */
    @Async
    public void profileEmail(String userId, String newEmail) throws MessagingException {
        // 查询当前用户
        UserProfile user = userProfileDao.findById(userId).orElse(null);
        if (null == user) {
            throw new RuntimeException("无此用户");
        }
        /**
         * 生成验证Token
         */
        // 获取当前时间
        long currentTime = System.currentTimeMillis();
        // 将Token跟当前时间结合
        String authKey = token + "|" + currentTime;
        // 加密
        String secretKey = MD5Utils.md5(authKey);
        secretKey = secretKey + ";" + currentTime;
        // 生成连接
        String link = hostPath + "/user/user/auth_email?email=" + newEmail + "&userId=" + userId + "&authKey=" + secretKey;
        String msg = "";
        try {
            URL url = Thread.currentThread().getContextClassLoader().getResource("");
            String filePath = url.getPath().replace("/target/classes/", "").substring(1);
            filePath = URLDecoder.decode(filePath, "UTF-8");
            byte[] data = Files.readAllBytes(Paths.get(filePath + "/src/main/resources/template/profile_email.html"));
            msg = new String(data, StandardCharsets.UTF_8);
            msg = msg.replace("{zw name zw}", user.getName()).replace("{zw link zw}", link);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 发送邮箱
        this.sendEmail("修改邮箱", msg, newEmail);
    }

    /**
     * 发送邮箱验证码
     */
    public void sendEmail(String object, String content, String email) throws MessagingException {
        // 创建一个复杂的邮件
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        // 参数一：MimeMessage
        // 参数二：是at否发送文件
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false);
        // 设置邮件信息
        helper.setSubject(object);
        helper.setText(content, true);         // 参数二：允许html
        helper.setTo(email);
        helper.setFrom(email_from);
        // 发送
        javaMailSender.send(mimeMessage);
    }

    /**
     * 确认修改邮箱
     * @param email
     * @param user_id
     * @param auth_key
     */
    public String comfirmProfileEmail(String email, String user_id, String auth_key) {
        // 验证authKey
        String[] split = auth_key.split(";");
        String authKey = token + "|" + split[1];
        String secretKey = MD5Utils.md5(authKey);
        // 检查时间是否超时
        long currentTime = System.currentTimeMillis();
        if (currentTime - Long.parseLong(split[1]) > 900000) {
            return "认证超时，请重新前往修改";
        }
        if (secretKey.equals(split[0])) {
            // 验证通过
            // 修改邮箱
            UserProfile user = userProfileDao.findById(user_id).orElse(null);
            user.setEmail(email);
            userProfileDao.save(user);
            return "邮箱修改成功";
        }
        // 验证不通过
        return "验证失败，请重试";
    }

    /**
     * 上传文件
     * @param userId
     * @param avatar
     * @return
     */
    public String profileAvatar(String userId, MultipartFile avatar) {
        // 查询user
        UserProfile user = userProfileDao.findById(userId).orElse(null);
        if (null == user) {
            throw new RuntimeException("没有该用户");
        }
        String surfix = avatar.getOriginalFilename().split("\\.")[1];
        String filePath = FileResource.getUploadPath("userAvatar") + File.separator + user.getId() + "." + surfix;
        // 将文件保存到指定目录
        try {
            Files.write(Paths.get(filePath), avatar.getBytes(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("上传图片失败");
        }
        // 将图片路径存入数据库
        String reDisplay = hostPath + "/userAvatar/" + user.getId() + "." + surfix;
        user.setAvatar(reDisplay);
        userProfileDao.save(user);
        return reDisplay;
    }

    /**
     * 删除好友
     * @param userId
     * @param friendId
     */
    public void deleteFriend(String userId, String friendId) {
        Friend friend = friendDao.findByUserIdAndFriendId(userId, friendId);
        if (null == friend) {
            throw new RuntimeException("查询不到该好友");
        }
        // 删除
        friendDao.delete(friend);
    }
}
