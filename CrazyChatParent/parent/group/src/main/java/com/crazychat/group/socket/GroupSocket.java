package com.crazychat.group.socket;

import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@ServerEndpoint(value = "/verify_group/{userId}")
public class GroupSocket {
    public static Map<String, Session> userCollect = new HashMap<>();

    @OnOpen
    public void onOpen(@PathParam("userId") String userId, Session session) {
        // 添加到map中
        userCollect.put(userId, session);
    }

    @OnClose
    public void onClose(@PathParam("userId") String userId, Session session) throws IOException {
        userCollect.remove(userId);
        session.close();
    }
}
