package com.crazychat.user.controller;

import com.crazychat.common.entity.Result;
import com.crazychat.common.entity.StatusCode;
import com.crazychat.common.utils.JwtUtils;
import com.crazychat.user.pojo.AdminUser;
import com.crazychat.user.service.AdminUserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
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
     *
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
     *
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


    /**
     * 给所有用户推送公告
     *
     * @param request
     * @param map
     * @return
     */
    @PostMapping("/send_nitice_to_all")
    public Result authSendNoticeToAll(HttpServletRequest request, @RequestBody Map map) {
        // 获取待发送的消息
        String message = (String) map.get("message");
        adminUserService.sendNoticeToAll(message);
        return new Result(true, StatusCode.OK.getCode(), "查询成功");
    }


    /**
     * 给选中的用户推送消息
     *
     * @param request
     * @param map
     * @return
     */
    @PostMapping("/send_notice_to_select")
    public Result authSendNoticeToSelect(HttpServletRequest request, @RequestBody Map map) {
        // 获取要推送的消息
        String message = (String) map.get("message");
        // 获取要推送的用户
        List<String> remoteUser = (List<String>) map.get("selectList");
        adminUserService.sendNoticeToSelect(message, remoteUser);
        return new Result(true, StatusCode.OK.getCode(), "发送成功");
    }


    /**
     * 修改管理员密码
     *
     * @param request
     * @param adminId
     * @param map
     * @return
     */
    @PutMapping("/modify_password/{adminId}")
    public Result authModifyPassword(HttpServletRequest request, @PathVariable String adminId, @RequestBody Map map) {
        // 获取新密码
        String newPassword = (String) map.get("password");
        adminUserService.ModifyPassword(adminId, newPassword);
        return new Result(true, StatusCode.OK.getCode(), "修改成功");
    }


    /**
     * 创建账号
     *
     * @param request
     * @param map
     * @return
     */
    @PostMapping("/create_account")
    public Result authCreateAccount(HttpServletRequest request, @RequestBody Map map) {
        // 获取密码跟用户名
        String adminName = (String) map.get("adminName");
        String password = (String) map.get("password");
        adminUserService.createAccount(adminName, password);
        return new Result(true, StatusCode.OK.getCode(), "账号创建成功");
    }


    /**
     * 加载所有的通知
     *
     * @param userId
     * @return
     */
    @GetMapping("/load_notice/{user_id}")
    public Result loadNotice(@PathVariable("user_id") String userId) {
        List<Map<String, String>> data = adminUserService.loadNotice(userId);
        return new Result(true, StatusCode.OK.getCode(), "查询成功", data);
    }


    /**
     * 将公告标为已读
     *
     * @param map
     * @return
     */
    @PutMapping("/read_notice")
    public Result readNotice(@RequestBody Map map) {
        // 获取key
        String noticeKey = (String) map.get("noticeKey");
        adminUserService.readNotice(noticeKey);
        return new Result(true, StatusCode.OK.getCode(), "成功标为已读");
    }


    /**
     * 将公告标为已读
     *
     * @param map
     * @return
     */
    @PutMapping("/no_read_notice")
    public Result noReadNotice(@RequestBody Map map) {
        // 获取key
        String noticeKey = (String) map.get("noticeKey");
        adminUserService.noReadNotice(noticeKey);
        return new Result(true, StatusCode.OK.getCode(), "成功标为未读");
    }


    /**
     * 删除公告
     *
     * @param map
     * @return
     */
    @PutMapping("/delete_notice")
    public Result deleteNotice(@RequestBody Map map) {
        // 获取key
        String noticeKey = (String) map.get("noticeKey");
        adminUserService.deleteNotice(noticeKey);
        return new Result(true, StatusCode.OK.getCode(), "公告删除成功");
    }


    /**
     * 查看是否有未读消息
     *
     * @param userId
     * @return
     */
    @GetMapping("/have_no_read_notice/{user_id}")
    public Result haveNoReadNotice(@PathVariable("user_id") String userId) {
        boolean data = adminUserService.haveNoReadNotice(userId);
        return new Result(true, StatusCode.OK.getCode(), "查询成功", data);
    }
}
