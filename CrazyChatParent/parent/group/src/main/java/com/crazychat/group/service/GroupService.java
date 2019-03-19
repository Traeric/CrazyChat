package com.crazychat.group.service;

import com.crazychat.common.utils.IdWorker;
import com.crazychat.group.client.UserClient;
import com.crazychat.group.dao.ChatRecordDao;
import com.crazychat.group.dao.GroupDao;
import com.crazychat.group.dao.GroupUserDao;
import com.crazychat.group.pojo.ChatRecord;
import com.crazychat.group.pojo.Group;
import com.crazychat.group.pojo.GroupUser;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.*;

@Service
@Transactional
public class GroupService {
    @Resource
    private GroupUserDao groupUserDao;

    @Resource
    private GroupDao groupDao;

    @Resource
    private ChatRecordDao chatRecordDao;

    @Resource
    private UserClient userClient;

    @Resource(name = "idWorker")
    private IdWorker idCreater;

    /**
     * 查询用户所在的群聊
     * @param userId
     * @return
     */
    public List<Map<String, Object>> getGroupListbyId(String userId) {
        // 查询该id所在的所有群
        List<GroupUser> groupUsers = groupUserDao.findAllByUserId(userId);
        // 需要返回的数据
        List<Map<String, Object>> data = new LinkedList<>();
        groupUsers.parallelStream().forEach((groupUser) -> {
            // 存放每一条数据
            Map<String, Object> map = new HashMap<>();
            map.put("id", groupUser.getGroupId());
            Group group = groupDao.findById(groupUser.getGroupId()).orElse(null);
            map.put("name", group != null ? group.getName() : "");     // 群名
            map.put("picture", group != null ? group.getPicture() : "");     // 群头像
            map.put("type", groupUser.getType());
            data.add(map);
        });
        return data;
    }

    /**
     * 用过id获取群
     * @param groupId
     * @return
     */
    public Group getGroupById(String groupId) {
        return groupDao.findById(groupId).orElse(null);
    }

    /**
     * 获取最后一条群聊天消息
     * @param groupId
     * @return
     */
    public Map<String, String> getLastMessage(String groupId) {
        List<ChatRecord> records = chatRecordDao.findAllByGroupId(groupId);
        ChatRecord record = records.get(records.size() - 1);
        Map<String, String> map = new HashMap<>();
        // 获取用户信息，远程调用user模块实现
        String userName = userClient.getUserNameById(record.getUserId());
        map.put("user", userName);
        map.put("msg", record.getContent());
        return map;
    }

    /**
     * 搜索群名
     * @param groupName
     * @return
     */
    public List<Map<String, String>> searchGroup(String groupName) {
        // 获取符合条件的群
        List<Group> groups = groupDao.findAllByNameContains(groupName);
        List<Map<String, String>> data = new ArrayList<>();
        groups.parallelStream().forEach((group) -> {
            Map<String, String> map = new HashMap<>();
            map.put("id", group.getId());
            map.put("name", group.getName());
            map.put("avatar", group.getPicture());
            data.add(map);
        });
        return data;
    }

    /**
     * 获取群信息
     * @param groupId
     * @return
     */
    public Map<String, String> getGroupInfo(String groupId) {
        Group group = groupDao.findById(groupId).orElse(null);
        if (null == group) {
            throw new RuntimeException("查询失败");
        }
        Map<String, String> map = new HashMap<>();
        map.put("name", group.getName());
        map.put("picture", group.getPicture());
        map.put("create_time", String.valueOf(group.getCreateTime()));
        // 查询创建人
        GroupUser groupUser = groupUserDao.findByGroupIdAndType(group.getId(), "1");
        map.put("creater", groupUser.getUserId());
        return map;
    }

    /**
     * 获取群成员
     * @param groupId
     * @return
     */
    public List<Map<String, String>> groupMembers(String groupId) {
        List<GroupUser> groupUsers = groupUserDao.findAllByGroupId(groupId);
        List<Map<String, String>> data = new ArrayList<>();
        groupUsers.parallelStream().forEach((groupUser) -> {
            Map<String, String> map = new HashMap<>();
            map.put("id", groupUser.getUserId());
            map.put("type", groupUser.getType());
            // 获取用户的头像以及昵称
            String name = userClient.getUserNameById(groupUser.getUserId());
            System.out.println(name);
            map.put("nick", name);
            String avatar = userClient.getUserAvatarById(groupUser.getUserId());
            map.put("avatar", avatar);
            data.add(map);
        });
        return data;
    }

    /**
     * 删除群成员
     * @param groupId
     * @param memberId
     */
    public void removeGroupMember(String groupId, String memberId) {
        GroupUser groupUser = groupUserDao.findByGroupIdAndUserId(groupId, memberId);
        groupUserDao.delete(groupUser);
    }

    /**
     * 删除群聊
     * @param groupId
     */
    public void deleteGroup(String groupId) {
        // 查询群聊
        Group group = groupDao.findById(groupId).orElse(null);
        if (null == group) {
            throw new RuntimeException("没有该群聊");
        }
        groupDao.delete(group);
    }

    /**
     * 创建群聊
     * @param groupName
     * @param createTime
     * @param createrId
     * @param groupMembers
     */
    public void createGroup(String groupName, Date createTime, String createrId, List<String> groupMembers) {
        // 创建一条群聊
        Group group = new Group();
        group.setId(String.valueOf(idCreater.nextId()));
        group.setName(groupName);
        group.setCreateTime(createTime);
        group.setPicture("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=4083465122,3106299428&fm=26&gp=0.jpg");
        groupDao.save(group);

        // 添加关联
        GroupUser self = new GroupUser();
        self.setId(String.valueOf(idCreater.nextId()));
        self.setUserId(createrId);
        self.setGroupId(group.getId());
        self.setType("1");
        groupUserDao.save(self);

        // 添加其他人的关联
        groupMembers.parallelStream().forEach((memberId) -> {
            GroupUser member = new GroupUser();
            member.setId(String.valueOf(idCreater.nextId()));
            member.setUserId(memberId);
            member.setGroupId(group.getId());
            member.setType("0");
            groupUserDao.save(member);
        });
    }
}
