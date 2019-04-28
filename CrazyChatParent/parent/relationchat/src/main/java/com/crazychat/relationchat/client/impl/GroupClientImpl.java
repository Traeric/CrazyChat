package com.crazychat.relationchat.client.impl;

import com.crazychat.relationchat.client.GroupClient;

import java.util.HashMap;
import java.util.Map;

public class GroupClientImpl implements GroupClient {

    @Override
    public byte[] getGroupNameById(String groupId) {
        return "NOT AVALIABLE".getBytes();
    }

    @Override
    public byte[] getGroupPictureById(String groupId) {
        return "NOT AVALIABLE".getBytes();
    }
}
