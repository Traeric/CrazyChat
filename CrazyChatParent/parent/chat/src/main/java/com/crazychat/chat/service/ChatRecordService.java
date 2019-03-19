package com.crazychat.chat.service;

import com.crazychat.chat.client.UserClient;
import com.crazychat.chat.dao.ChatRecordDao;
import com.crazychat.chat.dao.GroupChatRecordDao;
import com.crazychat.chat.pojo.ChatRecord;
import com.crazychat.chat.pojo.GroupChatRecord;
import com.crazychat.common.utils.IdWorker;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private IdWorker idWorker;


    /**
     * 获取用户最后一次聊天
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
     * 获取用户之间的聊天记录
     * @param userId
     * @param friendId
     * @return
     */
    public List<Map<String, String>> getChatRecord(String userId, String friendId) {
        List<ChatRecord> chatRecords = chatRecordDao.findAllByUserIdAndFriendId(userId, friendId);
        List<Map<String, String>> data = new ArrayList<>();
        chatRecords.parallelStream().forEach((chatReocrd) -> {
            Map<String, String> map = new HashMap<>();
            map.put("status", chatReocrd.getStatus());
            map.put("message", chatReocrd.getContent());
            data.add(map);
        });
        return data;
    }

    /**
     * 获取群聊信息
     * @param groupId
     * @return
     */
    public List<Map<String, String>> getGroupChatRecord(String groupId) {
        List<GroupChatRecord> chatGroups = groupChatRecordDao.findAllByGroupId(groupId);
        List<Map<String, String>> data = new ArrayList<>();
        chatGroups.parallelStream().forEach((groupChat) -> {
            Map<String, String> map = new HashMap<>();
            map.put("status", groupChat.getStatus());
            map.put("id", groupChat.getUserId());
            map.put("message", groupChat.getContent());
            // 获取用户名
            String name = userClient.getUserNameById(groupChat.getUserId());
            String avatar = userClient.getUserAvatarById(groupChat.getUserId());
            map.put("name", name);
            map.put("avatar", avatar);
            data.add(map);
        });
        return data;
    }


    /**
     * 发送消息给朋友
     * @param userId
     * @param friendId
     * @param message
     */
    public void sendMsgToUser(String userId, String friendId, String message) {
//        Session friend = (Session) redisTemplate.opsForValue().get(friendId);
//        if (null != friend) {
//            // 推送消息到朋友
//            friend.getAsyncRemote().sendText(message);
//        }
        // 保存消息到monogodb
        // 自己地方
        ChatRecord chatRecord = new ChatRecord();
        chatRecord.set_id(String.valueOf(idWorker.nextId()));
        chatRecord.setUserId(userId);
        chatRecord.setFriendId(friendId);
        chatRecord.setStatus("right");
        chatRecord.setContent(message);
        chatRecordDao.save(chatRecord);
        // 朋友一方
        chatRecord = new ChatRecord();
        chatRecord.set_id(String.valueOf(idWorker.nextId()));
        chatRecord.setUserId(friendId);
        chatRecord.setFriendId(userId);
        chatRecord.setContent(message);
        chatRecord.setStatus("left");
        chatRecordDao.save(chatRecord);
    }
}
