package com.crazychat.group.dao;

import com.crazychat.group.pojo.GroupUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface GroupUserDao extends JpaRepository<GroupUser, String>, JpaSpecificationExecutor<GroupUser> {
    List<GroupUser> findAllByUserId(String userId);
}
