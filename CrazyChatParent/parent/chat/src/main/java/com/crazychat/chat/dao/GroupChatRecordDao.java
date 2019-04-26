package com.crazychat.chat.dao;

import com.crazychat.chat.pojo.GroupChatRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface GroupChatRecordDao extends MongoRepository<GroupChatRecord, String> {
    // 获取群消息
    List<GroupChatRecord> findAllByGroupId(String groupId);

    /**
     * 查询所有的群聊记录
     * @param userId
     * @param groupId
     * @param startTime
     * @param endTime
     * @return
     */
    List<GroupChatRecord> findAllByUserIdAndGroupIdAndCreateTimeBetween(String userId, String groupId, Long startTime, Long endTime);
}
