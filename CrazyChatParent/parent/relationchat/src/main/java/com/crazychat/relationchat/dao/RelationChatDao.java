package com.crazychat.relationchat.dao;

import com.crazychat.relationchat.pojo.RelationChat;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RelationChatDao extends MongoRepository<RelationChat, String> {
    /**
     * 获取用户所有的最近聊天
     * @param userId
     * @return
     */
    List<RelationChat> findAllByUserId(String userId);
}
