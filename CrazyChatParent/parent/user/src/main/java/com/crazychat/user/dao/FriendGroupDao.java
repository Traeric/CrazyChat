package com.crazychat.user.dao;

import com.crazychat.user.pojo.FriendGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface FriendGroupDao extends JpaRepository<FriendGroup, String>, JpaSpecificationExecutor<FriendGroup> {
    List<FriendGroup> findAllByUserId(String userId);
}
