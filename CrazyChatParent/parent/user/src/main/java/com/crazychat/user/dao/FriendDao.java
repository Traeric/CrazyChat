package com.crazychat.user.dao;

import com.crazychat.user.pojo.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface FriendDao extends JpaRepository<Friend, String>, JpaSpecificationExecutor<Friend> {
    /**
     * 获取好友列表
     * @param userId
     * @param groupId
     * @return
     */
    List<Friend> findAllByFriendgroupId(String groupId);
}
