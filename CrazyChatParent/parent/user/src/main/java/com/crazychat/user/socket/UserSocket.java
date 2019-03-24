package com.crazychat.user.socket;

import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 推送消息验证，好友加群，好友加好友
 */
@Component
@ServerEndpoint(value = "/verify_user/{userId}")
public class UserSocket {
    public static Map<String, Session> userCollect = new HashMap<>();

    @OnOpen
    public void onOpen(@PathParam("userId") String userId, Session session) {
        // 将user保存进map
        userCollect.put(userId, session);
    }

    @OnClose
    public void onClose(@PathParam("userId") String userId, Session session) throws IOException {
        // 将连接从集合中去除
        userCollect.remove(userId);
        session.close();    // 关闭连接
    }
}
