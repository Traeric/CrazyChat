package com.crazychat.group.service;

import com.crazychat.common.utils.IdWorker;
import com.crazychat.group.client.UserClient;
import com.crazychat.group.dao.GroupDao;
import com.crazychat.group.dao.GroupUserDao;
import com.crazychat.group.pojo.Group;
import com.crazychat.group.pojo.GroupUser;
import com.crazychat.group.socket.GroupSocket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import javax.websocket.Session;
import java.util.*;

@Service
@Transactional
public class GroupService {
    @Resource
    private GroupUserDao groupUserDao;

    @Resource
    private GroupDao groupDao;

    @Resource
    private UserClient userClient;

    @Resource(name = "idWorker")
    private IdWorker idCreater;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 查询用户所在的群聊
     *
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
     *
     * @param groupId
     * @return
     */
    public Group getGroupById(String groupId) {
        return groupDao.findById(groupId).orElse(null);
    }

    /**
     * 搜索群名
     *
     * @param groupName
     * @param userId
     * @return
     */
    public List<Map<String, String>> searchGroup(String groupName, String userId) {
        // 获取符合条件的群
        List<Group> groups = groupDao.findAllByNameContains(groupName);
        List<Map<String, String>> data = new ArrayList<>();
        groups.parallelStream().forEach((group) -> {
            // 检查有没有加入该群
            boolean groupMember = this.isGroupMember(userId, group.getId());
            Map<String, String> map = new HashMap<>();
            map.put("id", group.getId());
            map.put("name", group.getName());
            map.put("avatar", group.getPicture());
            map.put("isGroupMember", groupMember ? "0" : "1");
            data.add(map);
        });
        return data;
    }

    /**
     * 获取群信息
     *
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
     *
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
            String name = new String(userClient.getUserNameById(groupUser.getUserId()));
            map.put("nick", name);
            String avatar = new String(userClient.getUserAvatarById(groupUser.getUserId()));
            map.put("avatar", avatar);
            data.add(map);
        });
        return data;
    }

    /**
     * 删除群成员
     *
     * @param groupId
     * @param memberId
     * @param type
     */
    public void removeGroupMember(String groupId, String memberId, String type) {
        GroupUser groupUser = groupUserDao.findByGroupIdAndUserId(groupId, memberId);
        groupUserDao.delete(groupUser);

        // 通知群
        Group group = groupDao.findById(groupId).orElse(null);
        String key = "";
        String confirmInfo = "";
        Session session = null;
        if ("0".equals(type)) {
            // 群主删除用户
            // 推送给用户
            key = memberId + "zw" + groupId + "delete";
            confirmInfo = "群主已经将你踢出" + group.getName() + "，青山不改，绿水长流，江湖再见！";
            session = GroupSocket.userCollect.get(memberId);
        } else if ("1".equals(type)) {
            // 用户主动退出群聊
            // 通知群主
            groupUser = groupUserDao.findByGroupIdAndType(groupId, "1");
            key = groupUser.getUserId() + "zw" + groupId + "delete";
            String name = new String(userClient.getUserNameById(memberId));
            confirmInfo = name + "已退出" + group.getName();
            session = GroupSocket.userCollect.get(groupUser.getUserId());
        }
        redisTemplate.delete(key);
        redisTemplate.opsForList().leftPushAll(key, confirmInfo, groupId, group.getPicture(),
                group.getName(), "3", "zw", "jx");
        if (null == session) {
            return;
        }
        // 封装消息
        String message = "[\"" + groupId + "\", \"" + confirmInfo + "\", \"" + group.getName() + "\", \"" +
                group.getPicture() + "\", \"3\"]";
        session.getAsyncRemote().sendText(message);
    }

    /**
     * 删除群聊
     *
     * @param groupId
     */
    public void deleteGroup(String groupId) {
        // 查询群聊
        Group group = groupDao.findById(groupId).orElse(null);
        if (null == group) {
            throw new RuntimeException("没有该群聊");
        }
        List<String> groupMemberIds = this.getGroupMember(group.getId());
        groupDao.delete(group);

        // 获取群主
        String createdId = groupUserDao.findByGroupIdAndType(groupId, "1").getUserId();
        String name = new String(userClient.getUserNameById(createdId));
        // 通知每一个人
        groupMemberIds.parallelStream().forEach((memberId) -> {
            if (!memberId.equals(createdId)) {
                // 通知每个人
                String key = memberId + "zw" + group.getId() + "delete";
                String confirmInfo = "群主" + name + "已经将" + group.getName() + "解散了，青山不改，绿水长流，兄弟们江湖再见！";
                redisTemplate.delete(key);
                redisTemplate.opsForList().leftPushAll(key, confirmInfo, group.getId(), group.getPicture(),
                        group.getName(), "3", "zw", "jx");
                Session session = GroupSocket.userCollect.get(memberId);
                if (null != session) {
                    // 封装消息
                    String message = "[\"" + group.getId() + "\", \"" + confirmInfo + "\", \"" + group.getName() + "\", \"" +
                            group.getPicture() + "\", \"3\"]";
                    session.getAsyncRemote().sendText(message);
                }
            }
        });
    }

    /**
     * 创建群聊
     *
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

        // 添加其他人的关联并通知每个人
        String name = new String(userClient.getUserNameById(createrId));
        groupMembers.parallelStream().forEach((memberId) -> {
            GroupUser member = new GroupUser();
            member.setId(String.valueOf(idCreater.nextId()));
            member.setUserId(memberId);
            member.setGroupId(group.getId());
            member.setType("0");
            groupUserDao.save(member);
            // 通知每个人
            String key = memberId + "zw" + group.getId() + "success";
            String confirmInfo = name + "将你拉入了" + group.getName();
            redisTemplate.delete(key);
            redisTemplate.opsForList().leftPushAll(key, confirmInfo, group.getId(), group.getPicture(),
                    group.getName(), "2", "zw", "jx");
            Session session = GroupSocket.userCollect.get(memberId);
            if (null != session) {
                // 封装消息
                String message = "[\"" + group.getId() + "\", \"" + confirmInfo + "\", \"" + group.getName() + "\", \"" +
                        group.getPicture() + "\", \"2\"]";
                session.getAsyncRemote().sendText(message);
            }
        });
    }

    /**
     * 获取群聊成员
     *
     * @param groupId
     * @return
     */
    public List<String> getGroupMember(String groupId) {
        List<GroupUser> groupUsers = groupUserDao.findAllByGroupId(groupId);
        List<String> data = new ArrayList<>();
        groupUsers.parallelStream().forEach((groupUser) -> data.add(groupUser.getUserId()));
        return data;
    }

    /**
     * 添加群聊申请
     *
     * @param userId
     * @param groupId
     * @param confirmInfo
     */
    public void addGroupApply(String userId, String groupId, String confirmInfo) {
        // 查询申请user的信息
        String name = new String(userClient.getUserNameById(userId));
        String avatar = new String(userClient.getUserAvatarById(userId));
        // 查询群主
        GroupUser groupUser = groupUserDao.findByGroupIdAndType(groupId, "1");
        String createrId = groupUser.getUserId();
        String groupName = groupDao.findById(groupId).orElse(null).getName();
        // 将验证消息存入redis中
        String key = createrId + "zw" + groupId;
        redisTemplate.delete(key);
        redisTemplate.opsForList().leftPushAll(key, confirmInfo, groupId, avatar, name, "1", groupName, userId);
        // 将消息推送给好友
        Session session = GroupSocket.userCollect.get(createrId);
        // 封装消息
        if (null == session) {
            return;
        }
        String message = "[\"" + groupId + "\", \"" + confirmInfo + "\", \"" + name +
                "\", \"" + avatar + "\", \"1\", \"" + groupName + "\", \"" + userId + "\"]";
        session.getAsyncRemote().sendText(message);
    }

    /**
     * 确定用户的加群申请
     *
     * @param groupId
     * @param applyId
     * @param userId
     */
    public void confirmAddGroup(String groupId, String applyId, String userId) {
        GroupUser groupUser = new GroupUser();
        groupUser.setId(String.valueOf(idCreater.nextId()));
        groupUser.setType("0");
        groupUser.setGroupId(groupId);
        groupUser.setUserId(applyId);
        groupUserDao.save(groupUser);
        // 从redis中删除
        String key = userId + "zw" + groupId;
        redisTemplate.delete(key);
        // 推送给用户
        Group group = groupDao.findById(groupId).orElse(null);
        key = applyId + "zw" + groupId + "success";
        String confirmInfo = "群主已经同意申请，您已是" + group.getName() + "的成员了。";
        redisTemplate.delete(key);
        redisTemplate.opsForList().leftPushAll(key, confirmInfo, groupId, group.getPicture(),
                group.getName(), "2", "zw", "jx");
        Session session = GroupSocket.userCollect.get(applyId);
        if (null == session) {
            return;
        }
        // 封装消息
        String message = "[\"" + groupId + "\", \"" + confirmInfo + "\", \"" + group.getName() + "\", \"" +
                group.getPicture() + "\", \"2\"]";
        session.getAsyncRemote().sendText(message);
    }

    /**
     * 检测用户是否是群成员
     *
     * @param userId
     * @param groupId
     * @return
     */
    public boolean isGroupMember(String userId, String groupId) {
        return null != groupUserDao.findByGroupIdAndUserId(groupId, userId);
    }

    /**
     * 查询群里聊数量
     *
     * @return
     */
    public Integer groupNum() {
        return groupDao.groupNum();
    }


    /**
     * 查询所有的群聊
     *
     * @param currentPage
     * @return
     */
    public Page<Group> findAllGroup(Integer currentPage) {
        Pageable pageable = PageRequest.of(currentPage - 1, 10);
        return groupDao.findAllGroup(pageable);
    }


    /**
     * 通过名字查询群聊
     *
     * @param userName
     * @param currentPage
     * @return
     */
    public Page<Group> findGroupByName(String userName, Integer currentPage) {
        Pageable pageable = PageRequest.of(currentPage - 1, 10);
        return groupDao.findAllByNameContains(userName, pageable);
    }


    /**
     * 管理员删除群聊
     *
     * @param groupId
     */
    public void adminDeleteGroup(String groupId) {
        // 查询群聊
        Group group = groupDao.findById(groupId).orElse(null);
        if (null == group) {
            throw new RuntimeException("没有该群聊");
        }
        // 获取所有的群聊
        List<String> groupMemberIds = this.getGroupMember(group.getId());
        groupDao.delete(group);

        // 通知每一个人
        groupMemberIds.parallelStream().forEach((memberId) -> {
            // 通知每个人
            String key = memberId + "zw" + group.getId() + "delete";
            String confirmInfo = "管理员已经将" + group.getName() + "解散了，青山不改，绿水长流，兄弟们江湖再见！";
            redisTemplate.delete(key);
            redisTemplate.opsForList().leftPushAll(key, confirmInfo, group.getId(), group.getPicture(),
                    group.getName(), "3", "zw", "jx");
            Session session = GroupSocket.userCollect.get(memberId);
            if (null != session) {
                // 封装消息
                String message = "[\"" + group.getId() + "\", \"" + confirmInfo + "\", \"" + group.getName() + "\", \"" +
                        group.getPicture() + "\", \"3\"]";
                session.getAsyncRemote().sendText(message);
            }
        });
    }
}
