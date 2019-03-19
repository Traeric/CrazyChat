package com.crazychat.group.controller;

import com.crazychat.common.entity.Result;
import com.crazychat.common.entity.StatusCode;
import com.crazychat.group.service.GroupService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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
     * 搜索群名
     * @param groupName
     * @return
     */
    @GetMapping("/search_group/{group_name}")
    public Result searchGroup(@PathVariable("group_name") String groupName) {
        List<Map<String, String>> data = groupService.searchGroup(groupName);
        return new Result(true, StatusCode.OK.getCode(), "搜索成功", data);
    }

    /**
     * 添加群聊
     * @param userId
     * @param groupId
     * @param map
     * @return
     */
    @PostMapping("/add_group/{user_id}/{group_id}")
    public Result addGroup(@PathVariable("user_id") String userId, @PathVariable("group_id") String groupId,
                           @RequestBody Map<String, String> map) {
        String confirmInfo = map.get("confirmInfo");
        return new Result(true, StatusCode.OK.getCode(), "添加成功");
    }

    /**
     * 查询群聊信息
     * @param groupId
     * @return
     */
    @GetMapping("/group_info/{group_id}")
    public Result groupInfo(@PathVariable("group_id") String groupId) {
        Map<String, String> data = groupService.getGroupInfo(groupId);
        return new Result(true, StatusCode.OK.getCode(), "查询成功", data);
    }

    /**
     * 获取所有的群成员
     * @param groupId
     * @return
     */
    @GetMapping("/get_members/{group_id}")
    public Result groupMembers(@PathVariable("group_id") String groupId) {
        List<Map<String, String>> data = groupService.groupMembers(groupId);
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
     * 获取最后一条消息
     * @param groupId
     * @return
     */
    @GetMapping("/get_last_msg/{group_id}")
    public Map<String, String> getLastMessage(@PathVariable("group_id") String groupId) {
        return groupService.getLastMessage(groupId);
    }

    /**
     * 删除群成员
     * @param groupId
     * @param memberId
     * @return
     */
    @DeleteMapping("/delete_member/{group_id}/{member_id}")
    public Result removeGroupMember(@PathVariable("group_id") String groupId, @PathVariable("member_id") String memberId) {
        groupService.removeGroupMember(groupId, memberId);
        return new Result(true, StatusCode.OK.getCode(), "删除成功");
    }

    /**
     * 删除群聊
     * @param groupId
     * @return
     */
    @DeleteMapping("/delete_group/{group_id}")
    public Result deleteGroup(@PathVariable("group_id") String groupId) {
        groupService.deleteGroup(groupId);
        return new Result(true, StatusCode.OK.getCode(), "解散成功");
    }

    /**
     * 创建群聊
     * @param map
     * @return
     */
    @PostMapping("/create_group")
    public Result createGroup(@RequestBody Map<String, Object> map) {
        String groupName = (String) map.get("groupName");
        String cTime = (String) map.get("createTime");
        String createrId = (String) map.get("createrId");
        List<String> groupMembers = (List<String>) map.get("groupMembers");
        SimpleDateFormat format = new SimpleDateFormat("y-M-d");
        Date createTime = null;
        try {
            createTime = format.parse(cTime.substring(0, 11));
            Calendar cl = Calendar.getInstance();
            cl.setTime(createTime);
            cl.add(Calendar.DAY_OF_YEAR, 1);
            createTime = cl.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        groupService.createGroup(groupName, createTime, createrId, groupMembers);
        return new Result(true, StatusCode.OK.getCode(), "创建成功");
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
}