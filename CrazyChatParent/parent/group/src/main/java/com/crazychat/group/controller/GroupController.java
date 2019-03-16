package com.crazychat.group.controller;

import com.crazychat.common.entity.Result;
import com.crazychat.common.entity.StatusCode;
import com.crazychat.group.service.GroupService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/group")
public class GroupController {
    @Resource
    private GroupService groupService;


    /**
     * 查询用户所在的群聊
     * @param userId
     * @return
     */
    @GetMapping("/group_list/{user_id}")
    public Result getGroupList(@PathVariable("user_id") String userId) {
        List<Map<String, Object>> data = groupService.getGroupListbyId(userId);
        return new Result(true, StatusCode.OK.getCode(), "查询成功", data);
    }

    /**
     * 获取群名
     * 该接口通过feign客户端远程调用
     * @param groupId
     * @return
     */
    @GetMapping("/name/{group_id}")
    public String getGroupNameById(@PathVariable("group_id") String groupId) {
        return groupService.getGroupById(groupId).getName();
    }

    /**
     * 获取群图片
     * 该接口通过feign客户端远程调用
     * @param groupId
     * @return
     */
    @GetMapping("/group_picture/{group_id}")
    public String getGroupPictureById(@PathVariable("group_id") String groupId) {
        return groupService.getGroupById(groupId).getPicture();
    }

    /**
     * 获取最后一条消息
     * @param groupId
     * @return
     */
    @GetMapping("/get_last_msg/{group_id}")
    public Map<String, String> getLastMessage(@PathVariable("group_id") String groupId) {
        return groupService.getLastMessage(groupId);
    }
}
