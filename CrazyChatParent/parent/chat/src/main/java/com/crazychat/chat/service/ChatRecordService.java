package com.crazychat.chat.service;

import com.crazychat.chat.client.GroupClient;
import com.crazychat.chat.client.RelationChatClient;
import com.crazychat.chat.client.UserClient;
import com.crazychat.chat.dao.ChatRecordDao;
import com.crazychat.chat.dao.GroupChatRecordDao;
import com.crazychat.chat.pojo.ChatRecord;
import com.crazychat.chat.pojo.GroupChatRecord;
import com.crazychat.chat.socket.UserToUserSocket;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.websocket.Session;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional
public class ChatRecordService {
    @Resource
    private ChatRecordDao chatRecordDao;

    @Resource
    private GroupChatRecordDao groupChatRecordDao;

    @Resource
    private UserClient userClient;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private GroupClient groupClient;

    @Resource
    private RelationChatClient relationChatClient;

    @Value("${web-resource-path}")
    private String webResourcePath;

    @Value("${url-path}")
    private String hostPath;


    /**
     * 获取用户最后一次聊天
     *
     * @param userId
     * @param friendId
     * @return
     */
    public String getLastMsg(String userId, String friendId) {
        List<ChatRecord> chatRecords = chatRecordDao.findAllByUserIdAndFriendId(userId, friendId);
        ChatRecord chatRecord = chatRecords.get(chatRecords.size() - 1);
        return chatRecord.getContent();
    }

    /**
     * 获取最后一条群聊信息
     *
     * @param groupId
     * @return
     */
    public Map<String, String> getLastMessage(String groupId) {
        List<GroupChatRecord> records = groupChatRecordDao.findAllByGroupId(groupId);
        GroupChatRecord groupChatRecord = records.get(records.size() - 1);
        Map<String, String> map = new HashMap<>();
        // 获取用户信息，远程调用user模块实现
        String userName = new String(userClient.getUserNameById(groupChatRecord.getUserId()));
        map.put("user", userName);
        map.put("msg", groupChatRecord.getContent());
        return map;
    }

    /**
     * 获取用户之间的聊天记录
     *
     * @param userId
     * @param friendId
     * @return
     */
    public List<Map<String, String>> getChatRecord(String userId, String friendId) {
        List<ChatRecord> chatRecords = chatRecordDao.findAllByUserIdAndFriendId(userId, friendId);
        List<Map<String, String>> data = new ArrayList<>();
        chatRecords.parallelStream().forEach((chatReocrd) -> {
            Map<String, String> map = new HashMap<>();
            map.put("id", String.valueOf(chatReocrd.getCreateTime()));
            map.put("status", chatReocrd.getStatus());
            map.put("message", chatReocrd.getContent());
            data.add(map);
        });
        return data;
    }

    /**
     * 获取群聊信息
     *
     * @param groupId
     * @return
     */
    public List<Map<String, String>> getGroupChatRecord(String groupId) {
        List<GroupChatRecord> chatGroups = groupChatRecordDao.findAllByGroupId(groupId);
        List<Map<String, String>> data = new ArrayList<>();
        chatGroups.parallelStream().forEach((groupChat) -> {
            Map<String, String> map = new HashMap<>();
            map.put("sortId", String.valueOf(groupChat.getCreateTime()));
            map.put("id", groupChat.getUserId());
            map.put("message", groupChat.getContent());
            // 获取用户名
            String name = new String(userClient.getUserNameById(groupChat.getUserId()));
            String avatar = new String(userClient.getUserAvatarById(groupChat.getUserId()));
            map.put("name", name);
            map.put("avatar", avatar);
            data.add(map);
        });
        return data;
    }

    /**
     * 发送消息给朋友
     *
     * @param userId
     * @param friendId
     * @param message
     */
    public void sendMsgToUser(String userId, String friendId, String message) {
        /**
         * 检测是否为好友
         */
        boolean haveFriendship = userClient.haveFriendship(userId, friendId);
        if (!haveFriendship) {
            throw new RuntimeException("没有好友关系");
        }
        // 检查是否是最近联系人
        boolean inRelationChat = relationChatClient.inRelationChat(userId, friendId);
        if (!inRelationChat) {
            // 添加最近联系人
            relationChatClient.addRealtionChat(friendId, userId, "0");
        }
        // 是好友，可以发送消息
        Session session = UserToUserSocket.userCollect.get(friendId);
        if (null != session) {
            // 推送消息给好友, zw@#$0发消息的头信息，0表示用户和用户进行发送
            String msg = "[\"" + userId + "\", \"0\", \"" + message + "\"]";
            this.sendToRedis(session, msg, friendId, userId);
        }
        // 保存消息到monogodb
        Long currentTime = System.currentTimeMillis();
        // 自己一方
        ChatRecord chatRecord = new ChatRecord();
        chatRecord.setUserId(userId);
        chatRecord.setFriendId(friendId);
        chatRecord.setStatus("right");
        chatRecord.setContent(message);
        chatRecord.setCreateTime(currentTime);
        chatRecordDao.save(chatRecord);
        // 朋友一方
        chatRecord = new ChatRecord();
        chatRecord.setUserId(friendId);
        chatRecord.setFriendId(userId);
        chatRecord.setContent(message);
        chatRecord.setStatus("left");
        chatRecord.setCreateTime(currentTime);
        chatRecordDao.save(chatRecord);
    }

    /**
     * 发送消息到群
     *
     * @param userId
     * @param groupId
     * @param message
     */
    public void sendMsgToGroup(String userId, String groupId, String message) {
        // 检测是否在成员群里面
        boolean isGroupMember = groupClient.isGroupMember(userId, groupId);
        if (!isGroupMember) {
            throw new RuntimeException("您已不属于该群");
        }
        // 在群里面，可以发送消息
        // 获取所有的群成员的id
        List<String> groupMemberList = groupClient.getGroupMemberList(groupId);
        // 推送消息给用户
        String name = new String(userClient.getUserNameById(userId));
        String avatar = new String(userClient.getUserAvatarById(userId));
        groupMemberList.parallelStream().forEach((groupMember) -> {
            if (!groupMember.equals(userId)) {
                // 检查是否添加了最近联系人
                boolean inRelation = relationChatClient.inRelationChat(groupMember, groupId);
                if (!inRelation) {
                    relationChatClient.addRealtionChat(groupMember, groupId, "1");
                }

                Session session = UserToUserSocket.userCollect.get(groupMember);
                if (null != session) {
                    String msg = "[\"" + groupId + "\", \"1\", \"" + name + "\", \"" + avatar + "\", \"" + message + "\"]";
                    this.sendToRedis(session, msg, groupMember, groupId);
                }
            }
        });
        // 保存消息
        GroupChatRecord groupChatRecord = new GroupChatRecord();
        groupChatRecord.setUserId(userId);
        groupChatRecord.setGroupId(groupId);
        groupChatRecord.setContent(message);
        groupChatRecord.setCreateTime(System.currentTimeMillis());
        groupChatRecordDao.save(groupChatRecord);
    }

    /**
     * 将消息存到redis
     *
     * @param session
     * @param message
     * @param prefix
     * @param suffix
     */
    private void sendToRedis(Session session, String message, String prefix, String suffix) {
        if (null != session) {
            // 该成员在线
            session.getAsyncRemote().sendText(message);
        } else {
            // 该成员不在线，保存到redis
            this.saveToRedis(prefix, suffix);
        }
    }

    /**
     * 更新未读消息到redis
     *
     * @param userId
     * @param otherId
     */
    public void addUnRead(String userId, String otherId) {
        this.saveToRedis(userId, otherId);
    }

    /**
     * 往redis中插入数据
     *
     * @param prefix
     * @param suffix
     */
    private void saveToRedis(String prefix, String suffix) {
        // 该成员不在线，保存到redis
        String key = prefix + "|" + suffix;
        // 查看该键是否存在
        if (null != redisTemplate.opsForValue().get(key)) {
            Integer num = (Integer) redisTemplate.opsForValue().get(key);
            redisTemplate.opsForValue().set(key, ++num);
        } else {
            redisTemplate.opsForValue().set(key, 1);
        }
    }

    /**
     * 删除未读记录
     *
     * @param userId
     * @param otherId
     */
    public void removeUnRead(String userId, String otherId) {
        String unReadKey = userId + "|" + otherId;
        // 删除key
        if (null != redisTemplate.opsForValue().get(unReadKey)) {
            redisTemplate.delete(unReadKey);
        }
    }


    /**
     * 删除指定的聊天记录
     *
     * @param userId
     * @param friendId
     */
    public void deleteChatRecord(String userId, String friendId) {
        // 获取用户对好友的聊天
        List<ChatRecord> userChat = chatRecordDao.findAllByUserIdAndFriendId(userId, friendId);
        // 删除
        userChat.parallelStream().forEach((record) -> chatRecordDao.delete(record));
        List<ChatRecord> friendChat = chatRecordDao.findAllByUserIdAndFriendId(friendId, userId);
        // 删除
        friendChat.parallelStream().forEach((record) -> chatRecordDao.delete(record));
    }


    /**
     * 上传聊天图片
     *
     * @param userId
     * @param image
     */
    public String uploadChatImage(String userId, MultipartFile image) {
        // 查询user
        String[] nameArr = image.getOriginalFilename().split("\\.");
        String surfix = nameArr[nameArr.length - 1];
        String fileName = System.currentTimeMillis() + "." + surfix;
        String filePath = webResourcePath + "chat_image/" + userId + "/" + fileName;
        // 将文件保存到指定目录
        try {
            if (!new File(webResourcePath + "chat_image/" + userId + "/").isDirectory()) {
                // 不是目录穿件目录
                new File(webResourcePath + "chat_image/" + userId + "/").mkdirs();
            }
            Files.write(Paths.get(filePath), image.getBytes(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("发送图片失败");
        }
        return hostPath + "/chat_image/" + userId + "/" + fileName;
    }


    /**
     * 上传聊天文件
     * @param userId
     * @param file
     * @return
     */
    public String uploadChatFile(String userId, MultipartFile file) {
        // 查询user
        String[] nameArr = file.getOriginalFilename().split("\\.");
        String surfix = nameArr[nameArr.length - 1];
        String fileName = System.currentTimeMillis() + "." + surfix;
        String filePath = webResourcePath + "chat_file/" + userId + "/" + fileName;
        // 将文件保存到指定目录
        try {
            if (!new File(webResourcePath + "chat_file/" + userId + "/").isDirectory()) {
                // 不是目录穿件目录
                new File(webResourcePath + "chat_file/" + userId + "/").mkdirs();
            }
            Files.write(Paths.get(filePath), file.getBytes(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("上传文件失败");
        }
        return hostPath + "/chat/chat/download_file/" + userId + "/" + fileName;
    }


    /**
     * 下载文件
     * @param userId
     * @param fileName
     * @return
     */
    public ResponseEntity downloadFile(String userId, String fileName) throws IOException {
        // 下载文件要使用FileSystemResource这个类
        // 构建文件路径
        String filePath = webResourcePath + "chat_file/" + userId + "/" + fileName;
        FileSystemResource file = new FileSystemResource(filePath);
        HttpHeaders httpHeaders = new HttpHeaders();
        // 在响应头中设置文件下载的名称
        httpHeaders.add("Content-Disposition", "attachment; filename=" + fileName);
        return ResponseEntity.ok()
                .headers(httpHeaders)
                .contentLength(file.contentLength())        // 下载文件的长度
                .contentType(MediaType.ALL.parseMediaType("application/octet-stream"))
                .body(new InputStreamResource(file.getInputStream()));
    }


    /**
     * 管理员查询用户对用户的聊天记录
     * @param fromUser
     * @param toUser
     * @param startTime
     * @param endTime
     * @return
     */
    public List<Map<String, Object>> getUserRecordByTime(String fromUser, String toUser, Long startTime, Long endTime) {
        List<ChatRecord> chatRecords = chatRecordDao.findAllByUserIdAndFriendIdAndCreateTimeBetween(fromUser, toUser, startTime, endTime);
        // 用户名
        String userName = new String(userClient.getUserNameById(fromUser));
        List<Map<String, Object>> data = new ArrayList<>();
        chatRecords.parallelStream().forEach((chatRecord) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("name", userName);
            map.put("content", chatRecord.getContent());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm ss秒");
            map.put("date", simpleDateFormat.format(new Date(chatRecord.getCreateTime())));
            data.add(map);
        });
        return data;
    }


    /**
     * 查询群聊的全部记录
     * @param userId
     * @param groupId
     * @param startTime
     * @param endTime
     * @return
     */
    public List<Map<String, Object>> getAllGroupRecordByTime(String userId, String groupId, Long startTime, Long endTime) {
        List<GroupChatRecord> groups = groupChatRecordDao.findAllByUserIdAndGroupIdAndCreateTimeBetween(userId, groupId, startTime, endTime);
        List<Map<String, Object>> data = new ArrayList<>();
        groups.parallelStream().forEach((group) -> {
            Map<String, Object> map = new HashMap<>();
            // 查询用户名
            String userName = new String(userClient.getUserNameById(group.getUserId()));
            map.put("name", userName);
            map.put("content", group.getContent());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm ss秒");
            map.put("date", simpleDateFormat.format(new Date(group.getCreateTime())));
            data.add(map);
        });
        return data;
    }
}
