package com.crazychat.chat.client;

import com.crazychat.chat.client.impl.RelationChatClientImpl;
import com.crazychat.common.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "crazychat-relationchat", fallback = RelationChatClientImpl.class)
public interface RelationChatClient {
    /**
     * 检查是否在最近联系人里面
     * @param userId
     * @param otherId
     * @return
     */
    @GetMapping("/relationchat/in_relation_chat/{user_id}/{other_id}")
    boolean inRelationChat(@PathVariable("user_id") String userId, @PathVariable("other_id") String otherId);

    /**
     * 删除最近联系人
     * @param userId
     * @param otherId
     * @return
     */
    @DeleteMapping("/relationchat/delete_chat/{user_id}/{chat_id}")
    Result deleteRelationChat(@PathVariable("user_id") String userId, @PathVariable("chat_id") String otherId);

    /**
     * 添加最近联系人
     * @param userId
     * @param otherId
     * @param type
     */
    @PostMapping("/relationchat/add_relation_chat/{user_id}/{other_id}/{type}")
    void addRealtionChat(@PathVariable("user_id") String userId, @PathVariable("other_id") String otherId,
                                @PathVariable("type") String type);
}
