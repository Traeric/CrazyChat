package com.crazychat.chat.client.impl;

import com.crazychat.chat.client.GroupClient;

import java.util.ArrayList;
import java.util.List;

public class GroupClientDao implements GroupClient {
    @Override
    public List<String> getGroupMemberList(String groupId) {
        return new ArrayList<>();
    }
}
