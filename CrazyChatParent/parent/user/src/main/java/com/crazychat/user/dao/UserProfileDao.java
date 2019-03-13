package com.crazychat.user.dao;

import com.crazychat.user.pojo.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserProfileDao extends JpaRepository<UserProfile, String>, JpaSpecificationExecutor<UserProfile> {
    // 根据邮箱查找
    UserProfile findByEmail(String email);
}
