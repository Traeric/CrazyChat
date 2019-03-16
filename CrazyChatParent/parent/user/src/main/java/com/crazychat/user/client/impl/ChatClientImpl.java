package com.crazychat.user.client.impl;

import com.crazychat.user.client.ChatClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatClientImpl implements ChatClient {
    @Override
    public List<Map<String, String>> getChatRecord(String userId, String friendId) {
        List<Map<String, String>> data = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        map.put("status", "left");
        map.put("message", "暂无数据");
        data.add(map);
        return data;
    }
}
