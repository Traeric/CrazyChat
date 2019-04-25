package com.crazychat.user.dao;

import com.crazychat.user.pojo.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface AdminUserDao extends JpaRepository<AdminUser, Integer>, JpaSpecificationExecutor<AdminUser> {
    AdminUser findByUsername(String username);

    @Query(value = "SELECT count(*) FROM admin_user", nativeQuery = true)
    Integer adminNum();
}
