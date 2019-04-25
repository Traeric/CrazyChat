package com.crazychat.user.service;

import com.crazychat.user.client.GroupClient;
import com.crazychat.user.dao.AdminUserDao;
import com.crazychat.user.dao.UserProfileDao;
import com.crazychat.user.pojo.AdminUser;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
public class AdminUserService {
    @Resource
    private AdminUserDao adminUserDao;

    @Resource
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Resource
    private GroupClient groupClient;

    @Resource
    private UserProfileDao userProfileDao;

    /**
     * 登陆后台用户
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
}
