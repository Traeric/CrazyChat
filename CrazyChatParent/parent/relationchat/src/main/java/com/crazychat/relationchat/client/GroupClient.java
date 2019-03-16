package com.crazychat.relationchat.client;

import com.crazychat.relationchat.client.impl.GroupClientImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@FeignClient(value = "crazychat-group", fallback = GroupClientImpl.class)
public interface GroupClient {

    /**
     * 获取群聊的名称
     * @param groupId
     * @return
     */
    @GetMapping("/group/name/{group_id}")
    String getGroupNameById(@PathVariable("group_id") String groupId);

    /**
     * 获取群聊的图片
     * @param groupId
     * @return
     */
    @GetMapping("/group/group_picture/{group_id}")
    String getGroupPictureById(@PathVariable("group_id") String groupId);

    /**
     * 获取群聊得到最后一个人的聊天
     * @param groupId
     * @return
     */
    @GetMapping("/group/get_last_msg/{group_id}")
    Map<String, String> getLastMessage(@PathVariable("group_id") String groupId);
}
