package com.crazychat.user.dao;

import com.crazychat.user.pojo.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface FriendDao extends JpaRepository<Friend, String>, JpaSpecificationExecutor<Friend> {
    /**
     * 获取好友列表
     * @param groupId
     * @param groupId
     * @return
     */
    List<Friend> findAllByFriendgroupId(String groupId);

    /**
     * 通过分组id和好友id查询好友与分组
     * @param groupId
     * @param friendId
     * @return
     */
    Friend findByFriendgroupIdAndFriendId(String groupId, String friendId);

    /**
     * 通过userId和friendId获取好友与分组
     * @param userId
     * @param friendId
     * @return
     */
    Friend findByUserIdAndFriendId(String userId, String friendId);
}
