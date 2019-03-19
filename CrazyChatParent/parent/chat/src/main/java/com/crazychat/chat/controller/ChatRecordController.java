package com.crazychat.chat.controller;

import com.crazychat.chat.service.ChatRecordService;
import com.crazychat.common.entity.Result;
import com.crazychat.common.entity.StatusCode;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/chat")
public class ChatRecordController {
    @Resource
    private ChatRecordService chatRecordService;

    /**
     * 获取用户和用户之间的最后一次聊天
     * 该接口供feign客户端远程调用
     * @param userId
     * @param friendId
     * @return
     */
    @GetMapping("/last_msg/{user_id}/{friend_id}")
    public String lastMsgUser2User(@PathVariable("user_id") String userId,
                                                @PathVariable("friend_id") String friendId) {
        return chatRecordService.getLastMsg(userId, friendId);
    }

    /**
     * 获取用户之间的聊天记录
     * 该接口供feign客户端远程调用
     * @param userId
     * @param friendId
     * @return
     */
    @GetMapping("/chat_record/{user_id}/{friend_id}")
    public List<Map<String, String>> getChatRecord(@PathVariable("user_id") String userId,
                                                   @PathVariable("friend_id") String friendId) {
        return chatRecordService.getChatRecord(userId, friendId);
    }

    /**
     * 获取群聊天记录
     * @param groupId
     * @return
     */
    @GetMapping("/group_record/{group_id}")
    public Result getGroupChatRecord(@PathVariable("group_id") String groupId) {
        List<Map<String, String>> data = chatRecordService.getGroupChatRecord(groupId);
        return new Result(true, StatusCode.OK.getCode(), "查询成功", data);
    }

    /**
     * 发送消息给好友
     * @param userId
     * @param friendId
     * @return
     */
    @PostMapping("/send_msgpersonnal/{user_id}/{friend_id}")
    public Result sendMsgToUser(@PathVariable("user_id") String userId, @PathVariable("friend_id") String friendId,
                                @RequestBody Map map) {
        String message = (String) map.get("message");
        chatRecordService.sendMsgToUser(userId, friendId, message);
        return new Result(true, StatusCode.OK.getCode(), "发送成功");
    }
}
