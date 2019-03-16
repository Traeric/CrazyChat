package com.crazychat.group.dao;

import com.crazychat.group.pojo.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GroupDao extends JpaRepository<Group, String>, JpaSpecificationExecutor<Group> {

}
