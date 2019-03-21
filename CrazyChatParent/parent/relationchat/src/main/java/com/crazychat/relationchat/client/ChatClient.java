package com.crazychat.relationchat.client;

import com.crazychat.relationchat.client.impl.ChatClientImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(value = "crazychat-chat", fallback = ChatClientImpl.class)
public interface ChatClient {

    /**
     * 获取两个用户之间最后一次聊天
     * @param userId
     * @param friendId
     * @return
     */
    @GetMapping("/chat/last_msg/{user_id}/{friend_id}")
    String lastMsgUser2User(@PathVariable("user_id") String userId,
                            @PathVariable("friend_id") String friendId);

    /**
     * 获取群聊得到最后一个人的聊天
     * @param groupId
     * @return
     */
    @GetMapping("/chat/get_last_msg/{group_id}")
    Map<String, String> getLastMessage(@PathVariable("group_id") String groupId);
}
