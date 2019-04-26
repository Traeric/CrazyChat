package com.crazychat.user.dao;

import com.crazychat.user.pojo.UserProfile;
import org.apache.catalina.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserProfileDao extends JpaRepository<UserProfile, String>, JpaSpecificationExecutor<UserProfile> {
    // 根据邮箱查找
    UserProfile findByEmail(String email);
    // 查询好友头像
    @Query(value = "SELECT avatar FROM userprofile WHERE id = ?1", nativeQuery = true)
    String getAvatar(String userId);

    List<UserProfile> findByNameContains(String userName);

    @Query(value = "SELECT count(*) FROM userprofile", nativeQuery = true)
    Integer userNum();

    @Query(value = "SELECT * FROM userprofile", nativeQuery = true)
    Page<UserProfile> findAllUser(Pageable pageable);

    Page<UserProfile> findAllByStatus(int status, Pageable pageable);

    Page<UserProfile> findAllByNameContains(String username, Pageable pageable);

    Page<UserProfile> findAllByNameContainsAndStatus(String username, Integer status, Pageable pageable);

    @Query(value = "UPDATE userprofile SET status = ?1 WHERE id = ?2", nativeQuery = true)
    @Modifying
    void changeStatus(Integer status, String userId);

    @Query(value = "SELECT * FROM userprofile WHERE status = 0", nativeQuery = true)
    List<UserProfile> findAllUser();
}
