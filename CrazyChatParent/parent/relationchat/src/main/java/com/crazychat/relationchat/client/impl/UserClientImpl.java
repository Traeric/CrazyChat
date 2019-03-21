package com.crazychat.relationchat.client.impl;

import com.crazychat.relationchat.client.UserClient;

public class UserClientImpl implements UserClient {
    @Override
    public byte[] getUserNameById(String userId) {
        return "NOT AVALIABLE".getBytes();
    }

    @Override
    public byte[] getUserAvatarById(String userId) {
        return "NOT AVALIABLE".getBytes();
    }

    @Override
    public byte[] getUserTodo(String userId, String friendId) {
        return "NOT AVALIABLE".getBytes();
    }
}
