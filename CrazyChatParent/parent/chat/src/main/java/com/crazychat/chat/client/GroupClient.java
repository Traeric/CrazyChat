package com.crazychat.chat.client;

import com.crazychat.chat.client.impl.GroupClientDao;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(value = "crazychat-group", fallback = GroupClientDao.class)
public interface GroupClient {
    /**
     * 获取群成员的id
     * @param groupId
     * @return
     */
    @GetMapping("/group/get_group_member/{group_id}")
    List<String> getGroupMemberList(@PathVariable("group_id") String groupId);
}
