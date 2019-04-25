package com.crazychat.group.dao;

import com.crazychat.group.pojo.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GroupDao extends JpaRepository<Group, String>, JpaSpecificationExecutor<Group> {
    List<Group> findAllByNameContains(String userName);

    // 查询所有的群聊数量
    @Query(value = "SELECT count(*) FROM group_chat", nativeQuery = true)
    Integer groupNum();

    @Query(value = "SELECT * FROM group_chat", nativeQuery = true)
    Page<Group> findAllGroup(Pageable pageable);
}
