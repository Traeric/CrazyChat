package com.crazychat.user.service;

import com.crazychat.common.utils.IdWorker;
import com.crazychat.user.client.GroupClient;
import com.crazychat.user.dao.AdminUserDao;
import com.crazychat.user.dao.UserProfileDao;
import com.crazychat.user.pojo.AdminUser;
import com.crazychat.user.pojo.UserProfile;
import com.crazychat.user.socket.UserSocket;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import javax.websocket.Session;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Transactional
public class AdminUserService {
    @Resource
    private AdminUserDao adminUserDao;

    @Resource
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Resource
    private GroupClient groupClient;

    @Resource
    private UserProfileDao userProfileDao;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private IdWorker idWorker;


    /**
     * 登陆后台用户
     *
     * @param username
     * @param password
     * @return
     */
    public AdminUser login(String username, String password) {
        // 根据用户名查询对象
        AdminUser admin = adminUserDao.findByUsername(username);
        // 对比密码
        if (admin != null && bCryptPasswordEncoder.matches(password, admin.getPassword())) {
            return admin;
        }
        return null;
    }


    /**
     * 统计数量
     *
     * @return
     */
    public Map<String, Object> countNum() {
        Map<String, Object> data = new HashMap<>();
        // 查询群聊数量
        data.put("groupNum", groupClient.groupNum());
        // 查询注册用户
        data.put("registerNum", userProfileDao.userNum());
        // 查询管理员数量
        data.put("adminNum", adminUserDao.adminNum());
        return data;
    }


    /**
     * 给所有用户发送公告
     *
     * @param message
     */
    public void sendNoticeToAll(String message) {
        List<UserProfile> users = userProfileDao.findAllUser();
        // 获取所有的socket
        Map<String, Session> userSocket = UserSocket.userCollect;
        // 创建日期
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        String date = simpleDateFormat.format(new Date());
        users.parallelStream().forEach((user) -> {
            // 生成键
            String key = user.getId() + "|notice|" + idWorker.nextId();
            // 将键值对保存到redis中
            redisTemplate.opsForList().leftPushAll(key, message, date, "0");      // 0是未读消息，1是已读
            /*
             * 对于在线的用户进行推送
             */
            Session session = userSocket.get(user.getId());
            if (null != session) {
                session.getAsyncRemote().sendText("zw");
            }
        });
    }


    /**
     * 推送公告给特定的用户
     *
     * @param message
     * @param remoteUser
     */
    public void sendNoticeToSelect(String message, List<String> remoteUser) {
        // 获取所有的socket
        Map<String, Session> userSocket = UserSocket.userCollect;
        // 创建日期
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        String date = simpleDateFormat.format(new Date());
        remoteUser.parallelStream().forEach((id) -> {
            // 生成键
            String key = id + "|notice|" + idWorker.nextId();
            // 将键值对保存到redis中
            redisTemplate.opsForList().leftPushAll(key, message, date, "0");      // 0是未读消息，1是已读
            /*
             * 对于在线的用户进行推送
             */
            Session session = userSocket.get(id);
            if (null != session) {
                // 推送消息
                session.getAsyncRemote().sendText("zw");
            }
        });
    }


    /**
     * 修改密码
     *
     * @param adminId
     * @param newPassword
     */
    public void ModifyPassword(String adminId, String newPassword) {
        // 设置密码加密
        String encodePassword = bCryptPasswordEncoder.encode(newPassword);
        adminUserDao.ModifyPaassword(encodePassword, adminId);
    }


    /**
     * 创建管理员账号
     *
     * @param adminName
     * @param password
     */
    public void createAccount(String adminName, String password) {
        // 查看账户是否已经存在
        if (null != adminUserDao.findByUsername(adminName)) {
            throw new RuntimeException("账户已存在！");
        }
        // 加密
        password = bCryptPasswordEncoder.encode(password);
        AdminUser adminUser = new AdminUser();
        adminUser.setUsername(adminName);
        adminUser.setPassword(password);
        adminUserDao.save(adminUser);
    }


    /**
     * 加载指定的通知
     *
     * @param userId
     * @return
     */
    public List<Map<String, String>> loadNotice(String userId) {
        // 获取所有满足要求的key
        String keyRegx = userId + "|notice|*";
        Set<String> keys = redisTemplate.keys(keyRegx);
        List<Map<String, String>> data = new ArrayList<>();
        if (null != keys) {
            keys.parallelStream().forEach((key) -> {
                List<String> list = redisTemplate.opsForList().range(key, 0, -1);
                Map<String, String> map = new HashMap<>();
                // 获取时间生成日期
                String title = list.get(1) + "通知";
                map.put("title", title);
                map.put("content", list.get(2));
                map.put("read", list.get(0));
                map.put("key", key);
                data.add(map);
            });
        }
        return data;
    }


    /**
     * 将公告标为已读
     *
     * @param noticeKey
     */
    public void readNotice(String noticeKey) {
        redisTemplate.opsForList().leftPop(noticeKey);
        redisTemplate.opsForList().leftPush(noticeKey, "1");   // 设置为已读
    }


    /**
     * 标为未读
     *
     * @param noticeKey
     */
    public void noReadNotice(String noticeKey) {
        redisTemplate.opsForList().leftPop(noticeKey);
        redisTemplate.opsForList().leftPush(noticeKey, "0");   // 设置为未读
    }


    /**
     * 删除公告
     *
     * @param noticeKey
     */
    public void deleteNotice(String noticeKey) {
        redisTemplate.delete(noticeKey);
    }


    /**
     * 查询是否有未读消息
     *
     * @param userId
     * @return
     */
    public boolean haveNoReadNotice(String userId) {
        AtomicBoolean flag = new AtomicBoolean(false);
        // 获取所有的key
        Set<String> keys = redisTemplate.keys(userId + "|notice|*");
        if (null != keys) {
            keys.parallelStream().forEach((key) -> {
                List<String> list = redisTemplate.opsForList().range(key, 0, -1);
                if ("0".equals(list.get(0))) {
                    flag.set(true);
                    return;
                }
            });
        }
        return flag.get();
    }
}
