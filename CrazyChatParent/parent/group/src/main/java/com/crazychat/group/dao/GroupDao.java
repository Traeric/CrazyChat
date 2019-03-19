package com.crazychat.group.dao;

import com.crazychat.group.pojo.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface GroupDao extends JpaRepository<Group, String>, JpaSpecificationExecutor<Group> {
    List<Group> findAllByNameContains(String userName);
}
