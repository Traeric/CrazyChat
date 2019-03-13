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
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/user/user")
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


}
