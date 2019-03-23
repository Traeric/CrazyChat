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
     * @param map
     * @return
     */
    @PostMapping("/send_msgpersonnal/{user_id}/{friend_id}")
    public Result sendMsgToUser(@PathVariable("user_id") String userId, @PathVariable("friend_id") String friendId,
                                @RequestBody Map map) {
        String message = (String) map.get("message");
        String removeFlag = (String) map.get("removeFlag");
        chatRecordService.sendMsgToUser(userId, friendId, message);
        return new Result(true, StatusCode.OK.getCode(), "发送成功", removeFlag);
    }

    /**
     * 发送消息到群里面
     * @param userId
     * @param groupId
     * @param map
     * @return
     */
    @PostMapping("/send_msggroup/{user_id}/{group_id}")
    public Result sendMsgToGroup(@PathVariable("user_id") String userId, @PathVariable("group_id") String groupId,
                                 @RequestBody Map map) {
        String message = (String) map.get("message");
        String removeFlag = (String) map.get("removeFlag");
        chatRecordService.sendMsgToGroup(userId, groupId, message);
        return new Result(true, StatusCode.OK.getCode(), "发送成功", removeFlag);
    }

    /**
     * 获取最后一条群聊消息
     * @param groupId
     * @return
     */
    @GetMapping("/get_last_msg/{group_id}")
    public Map<String, String> getLastMessage(@PathVariable("group_id") String groupId) {
        return chatRecordService.getLastMessage(groupId);
    }

    /**
     * 更新未读消息
     * @param userId
     * @param otherId
     * @return
     */
    @PutMapping("/add_unread/{user_id}/{other_id}")
    public Result addUnRead(@PathVariable("user_id") String userId, @PathVariable("other_id") String otherId) {
        chatRecordService.addUnRead(userId, otherId);
        return new Result(true, StatusCode.OK.getCode(), "更新成功");
    }

    /**
     * 删除redis上的未读记录
     * @param userId
     * @param otherId
     * @return
     */
    @DeleteMapping("/remove_unread/{user_id}/{other_id}")
    public Result removeUnRead(@PathVariable("user_id") String userId, @PathVariable("other_id") String otherId) {
        chatRecordService.removeUnRead(userId, otherId);
        return new Result(true, StatusCode.OK.getCode(), "删除成功");
    }
}
