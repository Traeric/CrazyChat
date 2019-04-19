package com.crazychat.user.client;

import com.crazychat.common.entity.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "crazychat-relationchat")
public interface RelationChatClient {
    @DeleteMapping("/relationchat/delete_chat/{user_id}/{chat_id}")
    Result deleteRelationChat(@PathVariable("user_id") String userId, @PathVariable("chat_id") String otherId);
}
