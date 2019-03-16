package com.crazychat.relationchat.controller;

import com.crazychat.common.entity.Result;
import com.crazychat.common.entity.StatusCode;
import com.crazychat.relationchat.service.RelationChatService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/relationchat")
public class RelationChatController {
    @Resource
    private RelationChatService relationChatService;

    /**
     * 获取最近聊天的列表
     * @param userId
     * @return
     */
    @GetMapping("/relation_chat/{user_id}")
    public Result getRelationChatList(@PathVariable("user_id") String userId) {
        List<Map<String, Object>> data = relationChatService.getRelatioChatList(userId);
        return new Result(true, StatusCode.OK.getCode(), "查询成功", data);
    }
}
