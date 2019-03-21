package com.crazychat.relationchat.client.impl;

import com.crazychat.relationchat.client.ChatClient;

import java.util.HashMap;
import java.util.Map;

public class ChatClientImpl implements ChatClient {

    @Override
    public String lastMsgUser2User(String userId, String friendId) {
        return "聊天内容失效";
    }

    @Override
    public Map<String, String> getLastMessage(String groupId) {
        Map<String, String> map = new HashMap<>();
        map.put("user", "");
        map.put("msg", "");
        return map;
    }
}
