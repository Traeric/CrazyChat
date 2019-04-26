package com.crazychat.user.dao;

import com.crazychat.user.pojo.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface AdminUserDao extends JpaRepository<AdminUser, Integer>, JpaSpecificationExecutor<AdminUser> {
    AdminUser findByUsername(String username);

    @Query(value = "SELECT count(*) FROM admin_user", nativeQuery = true)
    Integer adminNum();

    @Query(value = "UPDATE admin_user SET password = ?1 WHERE id = ?2", nativeQuery = true)
    @Modifying
    void ModifyPaassword(String password, String userId);
}
