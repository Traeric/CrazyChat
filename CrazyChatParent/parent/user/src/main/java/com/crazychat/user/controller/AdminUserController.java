package com.crazychat.user.controller;

import com.crazychat.common.entity.Result;
import com.crazychat.common.entity.StatusCode;
import com.crazychat.common.utils.JwtUtils;
import com.crazychat.user.pojo.AdminUser;
import com.crazychat.user.service.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/admin")
public class AdminUserController {
    @Resource
    private AdminUserService adminUserService;

    @Resource
    private JwtUtils jwtUtils;


    /**
     * 登陆
     * @param map
     * @return
     */
    @PostMapping("/admin_login")
    public Result login(@RequestBody Map map) {
        AdminUser adminUser = adminUserService.login((String) map.get("username"), (String) map.get("password"));
        if (null == adminUser) {
            return new Result(false, StatusCode.LOGINERROR.getCode(), StatusCode.LOGINERROR.getMsg());
        }
        // 登录成功，生成token
        String token = jwtUtils.createJWT(String.valueOf(adminUser.getId()), adminUser.getUsername(), "admin");
        // 生成返回信息
        Map<String, Object> data = new HashMap<>();
        data.put("id", adminUser.getId());
        data.put("token", token);
        data.put("username", adminUser.getUsername());
        return new Result(true, StatusCode.OK.getCode(), StatusCode.OK.getMsg(), data);
    }

    /**
     * 统计当前登陆的人数
     * @return
     */
    @GetMapping("/user_num")
    public Result authCountNum(HttpServletRequest request) {
        // 检查角色
        if (null == request.getAttribute("claims_admin")) {
            return new Result(false, StatusCode.ACCESSERROR.getCode(), StatusCode.ACCESSERROR.getMsg());
        }
        Map<String, Object> data = adminUserService.countNum();
        return new Result(true, StatusCode.OK.getCode(), "查询成功", data);
    }
}
