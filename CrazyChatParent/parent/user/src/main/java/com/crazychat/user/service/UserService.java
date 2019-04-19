package com.crazychat.user.service;

import com.crazychat.common.utils.IdWorker;
import com.crazychat.common.utils.MD5Utils;
import com.crazychat.user.client.ChatClient;
import com.crazychat.user.client.RelationChatClient;
import com.crazychat.user.dao.FriendDao;
import com.crazychat.user.dao.FriendGroupDao;
import com.crazychat.user.dao.UserProfileDao;
import com.crazychat.user.pojo.Friend;
import com.crazychat.user.pojo.FriendGroup;
import com.crazychat.user.pojo.UserProfile;
import com.crazychat.user.socket.UserSocket;
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
import javax.websocket.Session;
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

    @Resource
    private RelationChatClient relationChatClient;

    @Value("${auth-key}")
    private String token;

    @Value("${url-path}")
    private String hostPath;

    @Value("${web-resource-path}")
    private String webResourcePath;

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
     *
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
     *
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
            userProfileDao.save(user);   // 保存
            // 创建默认分组
            this.addGroup(user.getId(), "我的好友");
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
     *
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
     *
     * @param userId
     * @return
     */
    public UserProfile getNameById(String userId) {
        return userProfileDao.findById(userId).orElse(null);
    }

    /**
     * 获取用户信息
     *
     * @param userId
     * @return
     */
    public UserProfile getUserInfo(String userId) {
        return userProfileDao.findById(userId).orElse(null);
    }

    /**
     * 查询用户聊天记录
     *
     * @param userId
     * @param friendId
     * @return
     */
    public List<Map<String, String>> getChatRecord(String userId, String friendId) {
        return chatClient.getChatRecord(userId, friendId);
    }

    /**
     * 获取用户分组
     *
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
     *
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
     *
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
     * 搜索用户
     * @param userName
     * @param userId
     * @return
     */
    public List<Map<String, String>> searchUser(String userName, String userId) {
        List<UserProfile> users = userProfileDao.findByNameContains(userName);
        List<Map<String, String>> data = new ArrayList<>();
        users.parallelStream().forEach((user) -> {
            // 检查用户与当前好友是否建立了关系
            boolean isFriend = this.haveFriendship(userId, user.getId());
            Map<String, String> map = new HashMap<>();
            map.put("id", user.getId());
            map.put("name", user.getName());
            map.put("avatar", user.getAvatar());
            map.put("sign", user.getSign());
            map.put("isFriend", isFriend ? "0" : "1");
            data.add(map);
        });
        return data;
    }

    /**
     * 修改昵称
     *
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
     *
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
     *
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
     *
     * @param groupId
     * @return
     */
    public boolean groupIsEmpty(String groupId) {
        List<Friend> friends = friendDao.findAllByFriendgroupId(groupId);
        return friends.size() == 0;
    }

    /**
     * 删除分组
     *
     * @param groupId
     */
    public void deleteGroup(String groupId) {
        friendGroupDao.deleteById(groupId);
    }

    /**
     * 修改邮箱
     *
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
     *
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
     * 上传图片
     *
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
        String filePath = webResourcePath + "user_avatar/" + user.getId() + "." + surfix;
        // 将文件保存到指定目录
        try {
            if (!new File(webResourcePath + "user_avatar/").isDirectory()) {
                // 不是目录穿件目录
                new File(webResourcePath + "user_avatar/").mkdirs();
            }
            Files.write(Paths.get(filePath), avatar.getBytes(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("上传图片失败");
        }
        // 将图片路径存入数据库
        String reDisplay = hostPath + "/user_avatar/" + user.getId() + "." + surfix;
        user.setAvatar(reDisplay);
        userProfileDao.save(user);
        return reDisplay;
    }

    /**
     * 删除好友
     *
     * @param userId
     * @param friendId
     */
    public void deleteFriend(String userId, String friendId) {
        Friend friend = friendDao.findByUserIdAndFriendId(userId, friendId);
        Friend user = friendDao.findByUserIdAndFriendId(friendId, userId);
        if (null == friend || null == user) {
            throw new RuntimeException("查询不到该好友");
        }
        // 删除
        friendDao.delete(friend);
        friendDao.delete(user);

        // 给对方发送消息
        // 将验证消息存到redis中
        String key = friendId + "zw" + userId + "delete";
        String confirmInfo = "对方已将你删除，青山不改，绿水长流，江湖再见！";
        UserProfile userProfile = userProfileDao.findById(userId).orElse(null);
        String name = userProfile.getName();
        String avatar = userProfile.getAvatar();
        redisTemplate.delete(key);
        redisTemplate.opsForList().leftPushAll(key, confirmInfo, userId, avatar, name, "3", "zw", "jx");
        // 将消息推送给对应的好友
        Session session = UserSocket.userCollect.get(friendId);
        if (null == session) {
            return;
        }
        // 封装消息
        String message = "[\"" + userId + "\", \"" + confirmInfo + "\", \"" + name + "\", \"" + avatar + "\", \"3\"]";
        session.getAsyncRemote().sendText(message);

        // 删除聊天记录跟最近联系人
        chatClient.deleteChatRecord(userId, friendId);
        relationChatClient.deleteRelationChat(userId, friendId);
        relationChatClient.deleteRelationChat(friendId, userId);
    }

    /**
     * 获取用户备注
     *
     * @param userId
     * @param friendId
     * @return
     */
    public byte[] getUserTodo(String userId, String friendId) {
        return friendDao.findByUserIdAndFriendId(userId, friendId).getTodo().getBytes();
    }

    /**
     * 添加好友
     *
     * @param userId
     * @param friendId
     * @param confirmInfo
     * @param todo
     * @param groupFriendId
     */
    public void addFriend(String userId, String friendId, String confirmInfo, String todo, String groupFriendId) {
        // 通过socket推送给目标用户
        // 查询用户头像以及昵称
        UserProfile user = userProfileDao.findById(userId).orElse(null);
        if (null == user) {
            throw new RuntimeException("查询失败");
        }
        String name = user.getName();
        String avatar = user.getAvatar();
        // 将验证消息存到redis中
        String key = friendId + "zw" + userId;
        redisTemplate.delete(key);
        redisTemplate.opsForList().leftPushAll(key, confirmInfo, userId, avatar, name, "0", todo, groupFriendId);
        // 将消息推送给对应的好友
        Session session = UserSocket.userCollect.get(friendId);
        if (null == session) {
            return;
        }
        // 封装消息
        String message = "[\"" + userId + "\", \"" + confirmInfo + "\", \"" + name + "\", \"" + avatar + "\", \"0\"]";
        session.getAsyncRemote().sendText(message);
    }

    /**
     * 从redis中删除验证信息
     *
     * @param userId
     * @param otherId
     * @param type
     */
    public void removeConfirmInfo(String userId, String otherId, String type) {
        String key;
        // 选择key生成的类型
        switch (type) {
            case "0":
            case "1":
                key = userId + "zw" + otherId;
                break;
            case "2":
                key = userId + "zw" + otherId + "success";
                break;
            case "3":
                key = userId + "zw" + otherId + "delete";
                break;
            default:
                key = "";
        }
        redisTemplate.delete(key);
    }

    /**
     * 同意添加好友
     *
     * @param userId
     * @param friendId
     */
    public void allowFriendApply(String userId, String friendId) {
        // 从redis中取出数据
        String key = userId + "zw" + friendId;
        List<Object> userInfo = redisTemplate.opsForList().range(key, 0, -1);
        // 从redis中删除该信息
        redisTemplate.delete(key);
        // 将好友信息存入数据库
        // 查询出好友的第一个分组
        FriendGroup friendGroup = friendGroupDao.findAllByUserId(userId).get(0);
        UserProfile user = userProfileDao.findById(friendId).orElse(null);
        Friend friend = new Friend();
        friend.setId(String.valueOf(idCreater.nextId()));
        friend.setFriendgroupId(friendGroup.getId());
        friend.setUserId(userId);
        friend.setTodo(user.getName());
        friend.setFriendId(friendId);
        friendDao.save(friend);
        // 为申请人添加好友
        friend.setId(String.valueOf(idCreater.nextId()));
        friend.setFriendgroupId((String) userInfo.get(0));
        friend.setUserId(friendId);
        friend.setTodo((String) userInfo.get(1));
        friend.setFriendId(userId);
        friendDao.save(friend);

        // 通知对方已经同意了好友申请
        // 将消息存入redis中
        key = friendId + "zw" + userId + "success";
        String confirmInfo = "好友已同意申请，你们可以开始聊天啦";
        user = userProfileDao.findById(userId).orElse(null);
        String name = user.getName();
        String avatar = user.getAvatar();
        redisTemplate.delete(key);
        redisTemplate.opsForList().leftPushAll(key, confirmInfo, userId,
                avatar, name, "2", "zw", "jx");
        // 推送消息
        Session session = UserSocket.userCollect.get(friendId);
        if (null == session) {
            return;
        }
        // 封装消息
        String message = "[\"" + userId + "\", \"" + confirmInfo + "\", \"" + name + "\", \"" + avatar + "\", \"2\"]";
        session.getAsyncRemote().sendText(message);
    }

    /**
     * 加载验证消息
     *
     * @param userId
     * @return
     */
    public List<Map<String, String>> loadVerify(String userId) {
        // 从redis中获取验证消息
        // 获取所有的key
        Set<String> keys = redisTemplate.keys(userId + "zw*");
        List<Map<String, String>> data = new ArrayList<>();
        keys.parallelStream().forEach((key) -> {
            // 获取redis中对应的值
            List<Object> list = redisTemplate.opsForList().range(key, 0, -1);
            Map<String, String> map = new HashMap<>();
            map.put("otherId", (String) list.get(5));
            map.put("name", (String) list.get(3));
            map.put("avatar", (String) list.get(4));
            map.put("confirmInfo", (String) list.get(6));
            map.put("type", (String) list.get(2));
            map.put("groupName", (String) list.get(1));
            map.put("applyId", (String) list.get(0));
            data.add(map);
        });
        return data;
    }

    /**
     * 检测是否具有好友关系
     * @param userId
     * @param friendId
     * @return
     */
    public boolean haveFriendship(String userId, String friendId) {
        return null != friendDao.findByUserIdAndFriendId(userId, friendId);
    }
}
