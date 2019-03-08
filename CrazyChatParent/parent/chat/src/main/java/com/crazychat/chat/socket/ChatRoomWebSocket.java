package com.crazychat.chat.socket;

import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.LinkedList;
import java.util.List;

@ServerEndpoint(value = "/chat_room")
@Component
public class ChatRoomWebSocket {

    private static List<Session> userList = new LinkedList<>();

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session) {
        // 一个用户登录
        // 生成用户名
        String currentTime = String.valueOf(System.currentTimeMillis());
        String surfix = currentTime.substring(currentTime.length() - 5);
        String userName = "游客" + surfix;
        session.getUserProperties().put("nick", userName);
        // 生成该用户的头像
        int number = (int) Math.floor(Math.random() * 8);
        session.getUserProperties().put("avatar", number);
        // 将用户加入列表
        userList.add(session);

        // 当前在线人数
        int onlineUser = userList.size();
        // 通知其他用户有人登录
        userList.parallelStream().forEach(user -> {
            String msg;
            if (user == session) {
                // 自己 @#$zw 用于与用户发送的聊天消息区分
                msg = "@#$zw " + session.getUserProperties().get("nick") + " i " + onlineUser + " " + number + " self ";
                // 对于新用户，要加载以前的所有老用户
                StringBuilder userListStr = new StringBuilder();
                for (Session allUser : userList) {
                    userListStr.append(allUser.getUserProperties().get("nick")).append("&")
                            .append(allUser.getUserProperties().get("avatar"))
                            .append("|");
                }
                msg += userListStr;
            } else {
                // 其他人
                msg = "@#$zw " + session.getUserProperties().get("nick") + " i " + onlineUser + " " + number;
            }
            user.getAsyncRemote().sendText(msg);
        });
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session) {
        // 移除游客
        userList.remove(session);
        // 通知其他游客
        int onlineUser = userList.size();
        userList.parallelStream().forEach(user -> {
            String msg = "@#$zw " + session.getUserProperties().get("nick") + " o " + onlineUser;
            user.getAsyncRemote().sendText(msg);
        });
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        // 收到消息，推送给其他聊天室内的成员
        userList.parallelStream().forEach(user -> {
            String msg;
            String nick = (String) session.getUserProperties().get("nick");
            int avatar = (int) session.getUserProperties().get("avatar");
            if (user == session) {
                // 如果是自己
                msg = "<div class=\"msg-right msg clear-float\">\n" +
                        "                        <div class=\"name\">" + nick + "</div>\n" +
                        "                        <div class=\"wrap\">\n" +
                        "                            <div class=\"header\">\n" +
                        "                                <img src=\"/_nuxt/assets/images/chatroom/zw" + avatar + ".png\" alt=\"NO IMG\">\n" +
                        "                            </div>\n" +
                        "                            <div class=\"body\">\n" +
                        "                                <div class=\"trangle\"></div>\n" +
                        "                                <div class=\"text\">\n" + message +
                        "                                </div>\n" +
                        "                            </div>\n" +
                        "                        </div>\n" +
                        "                    </div>";
            } else {
                // 如果是其他用户
                msg = "<div class=\"msg-left msg clear-float\">\n" +
                        "                        <div class=\"name\">" + nick + "</div>\n" +
                        "                        <div class=\"wrap\">\n" +
                        "                            <div class=\"header\">\n" +
                        "                                <img src=\"/_nuxt/assets/images/chatroom/zw" + avatar + ".png\" alt=\"NO IMG\">\n" +
                        "                            </div>\n" +
                        "                            <div class=\"body\">\n" +
                        "                                <div class=\"trangle\"></div>\n" +
                        "                                <div class=\"text\">\n" + message +
                        "                                </div>\n" +
                        "                            </div>\n" +
                        "                        </div>\n" +
                        "                    </div>";
            }
            // 推送消息
            user.getAsyncRemote().sendText(msg);
        });
    }
}
