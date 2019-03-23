package com.crazychat.user.socket;

import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.HashMap;
import java.util.Map;

/**
 * 推送消息验证，好友加群，好友加好友
 */
@Component
@ServerEndpoint(value = "/message_broadcast/{userId}")
public class UserSocket {
    public static Map<String, Session> userCollect = new HashMap<>();
    private String currentKey;

    @OnOpen
    public void onOpen(@PathParam("userId") String userId, Session session) {
        // 将user保存进map
        currentKey = userId + "|" + System.currentTimeMillis();
        userCollect.put(currentKey, session);
    }

    @OnClose
    public void onClose() {
        // 将连接从集合中去除
        userCollect.remove(currentKey);
    }
}
