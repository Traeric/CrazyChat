package com.crazychat.group.client.impl;

import com.crazychat.group.client.UserClient;
import org.springframework.stereotype.Component;

@Component
public class UserClientImpl implements UserClient {

    @Override
    public String getUserNameById(String userId) {
        return "Not Avaliable";
    }

    @Override
    public String getUserAvatarById(String userId) {
        return "Not Avaliable";
    }
}
