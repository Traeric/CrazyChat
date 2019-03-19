package com.crazychat.chat.dao;

import com.crazychat.chat.pojo.GroupChatRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface GroupChatRecordDao extends MongoRepository<GroupChatRecord, String> {
    // 获取群消息
    List<GroupChatRecord> findAllByGroupId(String groupId);
}
