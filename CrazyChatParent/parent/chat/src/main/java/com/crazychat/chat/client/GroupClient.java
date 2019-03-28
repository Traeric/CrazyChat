package com.crazychat.chat.client;

import com.crazychat.chat.client.impl.GroupClientImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(value = "crazychat-group", fallback = GroupClientImpl.class)
public interface GroupClient {
    /**
     * 获取群成员的id
     * @param groupId
     * @return
     */
    @GetMapping("/group/get_group_member/{group_id}")
    List<String> getGroupMemberList(@PathVariable("group_id") String groupId);

    /**
     * 检测用户是否在群里面
     * @param userId
     * @param groupId
     * @return
     */
    @GetMapping("/group/is_group_member/{user_id}/{group_id}")
    boolean isGroupMember(@PathVariable("user_id") String userId, @PathVariable("group_id") String groupId);
}
