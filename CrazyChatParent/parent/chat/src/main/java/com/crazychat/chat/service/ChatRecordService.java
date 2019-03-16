package com.crazychat.chat.service;

import com.crazychat.chat.dao.ChatRecordDao;
import com.crazychat.chat.pojo.ChatRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

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
}
