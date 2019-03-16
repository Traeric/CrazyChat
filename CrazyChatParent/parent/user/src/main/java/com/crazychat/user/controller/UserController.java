package com.crazychat.user.controller;

import com.crazychat.common.entity.Result;
import com.crazychat.common.entity.StatusCode;
import com.crazychat.common.utils.JwtUtils;
import com.crazychat.user.pojo.UserProfile;
import com.crazychat.user.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;
    @Resource
    private JwtUtils jwtUtils;


    /**
     * 用户登录
     * @param map
     * @return
     */
    @PostMapping("/login")
    public Result login(@RequestBody Map map) {
        UserProfile user = userService.login((String) map.get("email"), (String) map.get("password"));
        if (null == user) {
            return new Result(false, StatusCode.LOGINERROR.getCode(), StatusCode.LOGINERROR.getMsg());
        }
        // 登录成功
        // 生成token
        String token = jwtUtils.createJWT(user.getId(), user.getEmail(), "user");
        // 生成返回信息
        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("token", token);
        data.put("nick", user.getName());
        data.put("avatar", user.getAvatar());
        return new Result(true, StatusCode.OK.getCode(), StatusCode.OK.getMsg(), data);
    }

    /**
     * 发送邮箱验证码
     * @param map
     * @return
     */
    @PostMapping("/send_email")
    public Result sendEmail(@RequestBody Map map) {
        String email = (String) map.get("email");
        // 发送验证码
        try {
            userService.sendEmail(email);
        } catch (MessagingException e) {
            return new Result(false, StatusCode.ERROR.getCode(), "发送邮件失败");
        }
        return new Result(true, StatusCode.OK.getCode(), "发送成功，请在15分钟内填写");
    }

    /**
     * 用户注册
     * @param user
     * @param code
     * @return
     */
    @PostMapping("/register/{code}")
    public Result register(@RequestBody UserProfile user, @PathVariable String code) {
        userService.register(user, code);
        return new Result(true, StatusCode.OK.getCode(), "注册成功");
    }

    /**
     * 获取用户的好友列表
     * @param user_id
     * @return
     */
    @GetMapping("/friend_list/{user_id}")
    public Result userFriendList(@PathVariable String user_id) {
        Map<String, Object> data = userService.getFriendList(user_id);
        return new Result(true, StatusCode.OK.getCode(), "查询成功", data);
    }

    @GetMapping("/user_info/{user_id}")
    public Result userInfo(@PathVariable("user_id") String userId) {
        UserProfile user = userService.getUserInfo(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("avatar", user.getAvatar());
        data.put("nick", user.getName());
        data.put("sign", user.getSign());
        data.put("email", user.getEmail());
        data.put("gender", user.getGender());
        data.put("birthday", user.getBirthday());
        data.put("describe", user.getUserdescribe());
        return new Result(true, StatusCode.OK.getCode(), "查询成功", data);
    }

    /**
     * 通过id获取用户名
     * 该接口通过feign客户端远程调用
     * @param userId
     * @return
     */
    @GetMapping("/name/{user_id}")
    public String getUserNameById(@PathVariable("user_id") String userId) {
        return userService.getNameById(userId).getName();
    }

    /**
     * 通过id获取用户头像
     * 该接口通过feign客户端远程调用
     * @param userId
     * @return
     */
    @GetMapping("/user_id/{user_id}")
    public String getUserAvatarById(@PathVariable("user_id") String userId) {
        return userService.getNameById(userId).getAvatar();
    }
}
