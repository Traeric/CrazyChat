package com.crazychat.chat.service;

import com.crazychat.chat.dao.ChatRecordDao;
import com.crazychat.chat.pojo.ChatRecord;
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
}
