package com.crazychat.group.client.impl;

import com.crazychat.group.client.UserClient;
import org.springframework.stereotype.Component;

@Component
public class UserClientImpl implements UserClient {

    @Override
    public byte[] getUserNameById(String userId) {
        return "Not Avaliable".getBytes();
    }

    @Override
    public byte[] getUserAvatarById(String userId) {
        return "Not Avaliable".getBytes();
    }
}
