package com.crazychat.group.controller;

import com.crazychat.common.entity.Result;
import com.crazychat.common.entity.StatusCode;
import com.crazychat.group.pojo.Group;
import com.crazychat.group.service.GroupService;
import org.springframework.data.domain.Page;
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
     * 查询群聊数量，通过feign客户端查询
     * @return
     */
    @GetMapping("/group_num")
    public Integer groupNum() {
        return groupService.groupNum();
    }


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
     * @param userId
     * @return
     */
    @GetMapping("/search_group/{group_name}/{user_id}")
    public Result searchGroup(@PathVariable("group_name") String groupName, @PathVariable("user_id") String userId) {
        List<Map<String, String>> data = groupService.searchGroup(groupName, userId);
        return new Result(true, StatusCode.OK.getCode(), "搜索成功", data);
    }

    /**
     * 申请添加群聊
     * @param userId
     * @param groupId
     * @param map
     * @return
     */
    @PostMapping("/add_group/{user_id}/{group_id}")
    public Result addGroup(@PathVariable("user_id") String userId, @PathVariable("group_id") String groupId,
                           @RequestBody Map<String, String> map) {
        String confirmInfo = map.get("confirmInfo");
        groupService.addGroupApply(userId, groupId, confirmInfo);
        return new Result(true, StatusCode.OK.getCode(), "申请成功");
    }

    /**
     * 确定添加群聊
     * @param groupId
     * @param applyId
     * @return
     */
    @PostMapping("/confirm_add_group/{group_id}/{apply_id}/{user_id}")
    public Result confirmAddGroup(@PathVariable("group_id") String groupId, @PathVariable("apply_id") String applyId,
                                  @PathVariable("user_id") String userId) {
        groupService.confirmAddGroup(groupId, applyId, userId);
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
     * 删除群成员
     * @param groupId
     * @param memberId
     * @return
     */
    @DeleteMapping("/delete_member/{group_id}/{member_id}/{type}")
    public Result removeGroupMember(@PathVariable("group_id") String groupId, @PathVariable("member_id") String memberId,
                                    @PathVariable("type") String type) {
        groupService.removeGroupMember(groupId, memberId, type);
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

    /**
     * 获取群成员id
     * @param groupId
     * @return
     */
    @GetMapping("/get_group_member/{group_id}")
    public List<String> getGroupMemberList(@PathVariable("group_id") String groupId) {
        return groupService.getGroupMember(groupId);
    }

    /**
     * 检测成员是否在群里面
     * @param userId
     * @param groupId
     * @return
     */
    @GetMapping("/is_group_member/{user_id}/{group_id}")
    public boolean isGroupMember(@PathVariable("user_id") String userId, @PathVariable("group_id") String groupId) {
        return groupService.isGroupMember(userId, groupId);
    }


    /**
     * 获取所有的群聊
     * @param currentPage
     * @return
     */
    @GetMapping("/find_all/{page}")
    public Result authFindAllGroup(@PathVariable("page") Integer currentPage) {
        Page<Group> groups = groupService.findAllGroup(currentPage);
        Map<String, Object> data = new HashMap<>();
        data.put("allNum", groups.getTotalElements());
        data.put("rows", groups.getContent());
        return new Result(true, StatusCode.OK.getCode(), "查询成功", data);
    }
}
