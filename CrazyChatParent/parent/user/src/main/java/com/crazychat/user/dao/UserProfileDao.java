package com.crazychat.user.dao;

import com.crazychat.user.pojo.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface UserProfileDao extends JpaRepository<UserProfile, String>, JpaSpecificationExecutor<UserProfile> {
    // 根据邮箱查找
    UserProfile findByEmail(String email);
    // 查询好友头像
    @Query(value = "SELECT avatar FROM userprofile WHERE id = ?1", nativeQuery = true)
    String getAvatar(String userId);
}
