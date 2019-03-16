package com.crazychat.group.dao;

import com.crazychat.group.pojo.ChatRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatRecordDao extends MongoRepository<ChatRecord, String> {
    // 获取群聊天记录
    List<ChatRecord> findAllByGroupId(String groupId);
}
