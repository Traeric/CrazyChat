package com.crazychat.user.client;

import com.crazychat.user.client.impl.ChatClientImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@FeignClient(value = "crazychat-chat", fallback = ChatClientImpl.class)
public interface ChatClient {
    @GetMapping("/chat/chat_record/{user_id}/{friend_id}")
    List<Map<String, String>> getChatRecord(@PathVariable("user_id") String userId, @PathVariable("friend_id") String friendId);

    @DeleteMapping("/chat/delete_chat_record/{user_id}/{friend_id}")
    void deleteChatRecord(@PathVariable("user_id") String userId, @PathVariable("friend_id") String friendId);
}
