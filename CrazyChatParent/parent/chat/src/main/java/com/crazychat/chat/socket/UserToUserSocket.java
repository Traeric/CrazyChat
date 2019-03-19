package com.crazychat.chat.socket;

import com.crazychat.common.utils.SeriableUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.nio.charset.StandardCharsets;

@Component
@ServerEndpoint(value = "/chat_user_to_user/{userId}")
public class UserToUserSocket {

    /**
     * 连接时执行的方法
     * @param session
     */
    @OnOpen
    public void onOpen(@PathParam("userId") String userId, Session session) {
        Jedis jedis = new Jedis("192.168.117.128", 6379);
        jedis.connect();    // 链接
        // 保存用户到redis
        jedis.set(userId + "_chat", new String(SeriableUtil.toByteArray(session), StandardCharsets.UTF_8));
    }

    /**
     * 连接关闭时执行的方法
     * @param session
     */
    @OnClose
    public void onClose(@PathParam("userId") String userId, Session session) {
        // 从redis中删除缓存
        // redisTemplate.delete(userId + "_chat");
    }

    /**
     * 收到消息时的执行的方法
     * @param message
     * @param session
     */
    @OnMessage
    public void onMessage(String message, Session session) {}
}
