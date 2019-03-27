package com.crazychat.user.controller;

import com.crazychat.common.entity.Result;
import com.crazychat.common.entity.StatusCode;
import com.crazychat.common.utils.JwtUtils;
import com.crazychat.user.pojo.UserProfile;
import com.crazychat.user.service.UserService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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

    /**
     * 获取用户信息
     * @param userId
     * @return
     */
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
     * 获取用户聊天数据
     * @param userId
     * @param friendId
     * @return
     */
    @GetMapping("/chat_record/{user_id}/{friend_id}")
    public Result getChatRecord(@PathVariable("user_id") String userId, @PathVariable("friend_id") String friendId) {
        List<Map<String, String>> data = userService.getChatRecord(userId, friendId);
        return new Result(true, StatusCode.OK.getCode(), "查询成功", data);
    }

    /**
     * 查询用户分组
     * @param userId
     * @return
     */
    @GetMapping("/get_group_list/{user_id}")
    public Result getGroupList(@PathVariable("user_id") String userId) {
        List<Map<String, String>> data = userService.getGroupList(userId);
        return new Result(true, StatusCode.OK.getCode(), "查询成功", data);
    }

    /**
     * 修改分组
     * @param friendId
     * @param oldGroup
     * @param newGroup
     * @return
     */
    @PutMapping("/change_group/{user_id}/{old_group_id}/{new_group_id}")
    public Result changeGroup(@PathVariable("user_id") String friendId, @PathVariable("old_group_id") String oldGroup,
                              @PathVariable("new_group_id") String newGroup) {
        userService.changeGroup(friendId, oldGroup, newGroup);
        return new Result(true, StatusCode.OK.getCode(), "修改分组成功");
    }

    /**
     * 修改好友备注
     * @param userId
     * @param friendId
     * @return
     */
    @PutMapping("/change_todo/{user_id}/{friend_id}")
    public Result changeTodo(@PathVariable("user_id") String userId, @PathVariable("friend_id") String friendId,
                             @RequestBody Map<String, String> map) {
        String todo = map.get("todo");
        userService.changeTodo(userId, friendId, todo);
        return new Result(true, StatusCode.OK.getCode(), "修改备注成功");
    }

    /**
     * 搜索用户
     * @param userName
     * @param userId
     * @return
     */
    @GetMapping("/search_friend/{user_name}/{user_id}")
    public Result searchUser(@PathVariable("user_name") String userName, @PathVariable("user_id") String userId) {
        List<Map<String, String>> data = userService.searchUser(userName, userId);
        return new Result(true, StatusCode.OK.getCode(), "查询成功", data);
    }

    /**
     * 添加好友
     * @param userId
     * @param friendId
     * @param map
     * @return
     */
    @PostMapping("/add_friend/{user_id}/{friend_id}")
    public Result addFriend(@PathVariable("user_id") String userId, @PathVariable("friend_id") String friendId,
                            @RequestBody Map<String, String> map) {
        String confirmInfo = map.get("confirmInfo");
        String todo = map.get("todo");
        String groupFriendId = map.get("group");
        userService.addFriend(userId, friendId, confirmInfo, todo, groupFriendId);
        return new Result(true, StatusCode.OK.getCode(), "发送成功");
    }

    /**
     * 从redis中移除验证信息
     * @param userId
     * @param otherId
     * @return
     */
    @DeleteMapping("/remove_confirm_info/{user_id}/{other_id}/{type}")
    public Result removeConfirmInfo(@PathVariable("user_id") String userId, @PathVariable("other_id") String otherId,
                                    @PathVariable("type") String type) {
        userService.removeConfirmInfo(userId, otherId, type);
        return new Result(true, StatusCode.OK.getCode(), "删除成功");
    }

    /**
     * 同意好友申请
     * @param userId
     * @param friendId
     * @return
     */
    @PostMapping("/allow_friend_apply/{user_id}/{friend_id}")
    public Result allowFriendApply(@PathVariable("user_id") String userId, @PathVariable("friend_id") String friendId) {
        userService.allowFriendApply(userId, friendId);
        return new Result(true, StatusCode.OK.getCode(), "添加成功");
    }

    /**
     * 删除好友
     * @param userId
     * @param friendId
     * @return
     */
    @DeleteMapping("/delete_friend/{user_id}/{friend_id}")
    public Result deleteFriend(@PathVariable("user_id") String userId, @PathVariable("friend_id") String friendId) {
        userService.deleteFriend(userId, friendId);
        return new Result(true, StatusCode.OK.getCode(), "删除成功");
    }

    /**
     * 修改昵称
     * @param userId
     * @return
     */
    @PutMapping("/profile_nick/{user_id}")
    public Result profileNick(@PathVariable("user_id") String userId, @RequestBody Map<String, String> map) {
        String nick = map.get("nick");
        userService.profileNick(userId, nick);
        return new Result(true, StatusCode.OK.getCode(), "修改成功");
    }

    /**
     * 修改用户其他设置信息
     * @param userId
     * @param map
     * @return
     */
    @PutMapping("/profile_info/{user_id}")
    public Result profileInfo(@PathVariable("user_id") String userId, @RequestBody Map<String, String> map) {
        String sign = map.get("sign");
        String gender = map.get("gender");
        String birthday = map.get("birthday");
        String describe = map.get("describe");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date birth = null;
        try {
            birth = format.parse(birthday.substring(0, 11));
            Calendar cl = Calendar.getInstance();
            cl.setTime(birth);
            cl.add(Calendar.DAY_OF_YEAR, 2);   // 日期加一天
            birth = cl.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        userService.profileInfo(userId, sign, gender, birth, describe);
        return new Result(true, StatusCode.OK.getCode(), "修改成功");
    }

    /**
     * 新增分组
     * @param userId
     * @param map
     * @return
     */
    @PostMapping("/add_group/{user_id}")
    public Result addGroup(@PathVariable("user_id") String userId, @RequestBody Map<String, String> map) {
        String groupName = map.get("name");
        userService.addGroup(userId, groupName);
        return new Result(true, StatusCode.OK.getCode(), "新增分组成功");
    }

    /**
     * 查询当前分组下是否还有好友
     * @param groupId
     * @return
     */
    @GetMapping("/is_empty/{group_id}")
    public Result groupIsEmpty(@PathVariable("group_id") String groupId) {
        boolean data = userService.groupIsEmpty(groupId);
        return new Result(true, StatusCode.OK.getCode(), "查询成功", data);
    }

    /**
     * 删除分组
     * @param groupId
     * @return
     */
    @DeleteMapping("/delete_group/{group_id}")
    public Result deleteGroup(@PathVariable("group_id") String groupId) {
        userService.deleteGroup(groupId);
        return new Result(true, StatusCode.OK.getCode(), "删除成功");
    }

    /**
     * 修改邮箱
     * @param userId
     * @return
     */
    @PutMapping("/profile_email/{user_id}")
    public Result profileEmail(@PathVariable("user_id") String userId, @RequestBody Map map) throws MessagingException {
        String newEmail = (String) map.get("email");
        userService.profileEmail(userId, newEmail);
        return new Result(true, StatusCode.OK.getCode(), "修改成功，请在15分钟内前往新的邮箱进行验证");
    }

    /**
     * 确定修改邮箱
     * @param email
     * @param userId
     * @param authKey
     * @return
     */
    @GetMapping("/auth_email")
    public String confirmProfileEmail(String email, String userId, String authKey) {
        return userService.comfirmProfileEmail(email, userId, authKey);
    }

    /**
     * 修改用户头像
     * @param userId
     * @return
     */
    @PutMapping("/profile_avatar/{user_id}")
    public Result profileAvatar(@PathVariable("user_id") String userId, MultipartFile avatar) {
        String data = userService.profileAvatar(userId, avatar);
        return new Result(true, StatusCode.OK.getCode(), "修改成功", data);
    }

    /**
     * 加载验证消息
     * @param userId
     * @return
     */
    @GetMapping("/load_verify/{user_id}")
    public Result loadVerify(@PathVariable("user_id") String userId) {
        List<Map<String, String>> data = userService.loadVerify(userId);
        return new Result(true, StatusCode.OK.getCode(), "查询成功", data);
    }

    /**
     * 通过id获取用户名
     * 该接口通过feign客户端远程调用
     * @param userId
     * @return
     */
    @GetMapping("/name/{user_id}")
    public byte[] getUserNameById(@PathVariable("user_id") String userId) {
        return userService.getNameById(userId).getName().getBytes();
    }

    /**
     * 通过id获取用户头像
     * 该接口通过feign客户端远程调用
     * @param userId
     * @return
     */
    @GetMapping("/user_id/{user_id}")
    public byte[] getUserAvatarById(@PathVariable("user_id") String userId) {
        return userService.getNameById(userId).getAvatar().getBytes();
    }

    /**
     * 获取好友的备注
     * 该接口通过feign客户端远程调用
     * @param userId
     * @return
     */
    @GetMapping("/user_todo/{user_id}/{friend_id}")
    public byte[] getUserTodo(@PathVariable("user_id") String userId, @PathVariable("friend_id") String friendId) {
        return userService.getUserTodo(userId, friendId);
    }

    /**
     * 查询是否具有好友关系
     * 该接口通过feign客户端调用
     * @param userId
     * @param friendId
     * @return
     */
    @GetMapping("/have_friendship/{user_id}/{friend_id}")
    public boolean haveFriendship(@PathVariable("user_id") String userId, @PathVariable("friend_id") String friendId) {
        return userService.haveFriendship(userId, friendId);
    }
}
