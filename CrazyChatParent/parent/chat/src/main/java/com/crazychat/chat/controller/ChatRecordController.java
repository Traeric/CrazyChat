package com.crazychat.chat.controller;

import com.crazychat.chat.service.ChatRecordService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

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
}
