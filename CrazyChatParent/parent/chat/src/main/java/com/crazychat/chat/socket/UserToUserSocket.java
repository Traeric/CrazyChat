package com.crazychat.chat.socket;

import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.HashMap;
import java.util.Map;

@Component
@ServerEndpoint(value = "/chat_user_to_user/{userId}")
public class UserToUserSocket {
    public static Map<String, Session> userCollect = new HashMap<>();
    private String currentKey;

    /**
     * 连接时执行的方法
     * @param session
     */
    @OnOpen
    public void onOpen(@PathParam("userId") String userId, Session session) {
        // 将数据保存进map
        currentKey = userId + "|" + System.currentTimeMillis();
        userCollect.put(currentKey, session);
    }

    /**
     * 连接关闭时执行的方法
     * @param session
     */
    @OnClose
    public void onClose(@PathParam("userId") String userId, Session session) {
        // 从map中删除
        userCollect.remove(currentKey);
    }

    /**
     * 收到消息时的执行的方法
     * @param message
     * @param session
     */
    @OnMessage
    public void onMessage(String message, Session session) {}
}
