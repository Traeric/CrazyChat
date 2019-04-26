package com.crazychat.chat.dao;

import com.crazychat.chat.pojo.ChatRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatRecordDao extends MongoRepository<ChatRecord, String> {
    /**
     * 获取用户的聊天信息
     * @param userId
     * @param friendId
     * @return
     */
    List<ChatRecord> findAllByUserIdAndFriendId(String userId, String friendId);

    /**
     * 查询用户对用户的聊天记录
     * @param userId
     * @param friendId
     * @param startTime
     * @param endTime
     * @return
     */
    List<ChatRecord> findAllByUserIdAndFriendIdAndCreateTimeBetween(String userId, String friendId, Long startTime, Long endTime);
}


