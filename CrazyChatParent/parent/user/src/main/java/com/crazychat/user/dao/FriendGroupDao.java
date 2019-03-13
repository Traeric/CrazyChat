package com.crazychat.user.dao;

import com.crazychat.user.pojo.FriendGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FriendGroupDao extends JpaRepository<FriendGroup, String>, JpaSpecificationExecutor<FriendGroup> {
}
