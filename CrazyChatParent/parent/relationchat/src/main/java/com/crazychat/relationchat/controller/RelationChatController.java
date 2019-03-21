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

    /**
     * 添加最近联系人
     * @param userId
     * @param otherId
     * @return
     */
    @PostMapping("/add_chat/{user_id}/{chat_id}")
    public Result addRelationChat(@PathVariable("user_id") String userId, @PathVariable("chat_id") String otherId,
                                  @RequestBody Map map) {
        String type = String.valueOf(map.get("type"));
        relationChatService.addRelationChat(userId, otherId, type);
        return new Result(true, StatusCode.OK.getCode(), "添加成功");
    }

    /**
     * 删除最近联系人
     * @param userId
     * @param otherId
     * @return
     */
    @DeleteMapping("/delete_chat/{user_id}/{chat_id}")
    public Result deleteRelationChat(@PathVariable("user_id") String userId, @PathVariable("chat_id") String otherId) {
        relationChatService.deleteRelationChat(userId, otherId);
        return new Result(true, StatusCode.OK.getCode(), "删除成功");
    }
}
