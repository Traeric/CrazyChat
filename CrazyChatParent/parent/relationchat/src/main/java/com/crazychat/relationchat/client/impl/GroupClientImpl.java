package com.crazychat.relationchat.client.impl;

import com.crazychat.relationchat.client.GroupClient;

import java.util.HashMap;
import java.util.Map;

public class GroupClientImpl implements GroupClient {

    @Override
    public String getGroupNameById(String groupId) {
        return "NOT AVALIABLE";
    }

    @Override
    public String getGroupPictureById(String groupId) {
        return "NOT AVALIABLE";
    }

    @Override
    public Map<String, String> getLastMessage(String groupId) {
        Map<String, String> map = new HashMap<>();
        map.put("user", "");
        map.put("msg", "");
        return map;
    }
}
