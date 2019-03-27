package com.crazychat.chat.client.impl;

import com.crazychat.chat.client.UserClient;

public class UserClientImpl implements UserClient {
    @Override
    public byte[] getUserNameById(String userId) {
        return "NOT AVAILABLE".getBytes();
    }

    @Override
    public byte[] getUserAvatarById(String userId) {
        return "NOT AVAILABLE".getBytes();
    }

    @Override
    public boolean haveFriendship(String userId, String friendId) {
        return false;
    }
}
