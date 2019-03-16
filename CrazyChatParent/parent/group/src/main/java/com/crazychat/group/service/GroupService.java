package com.crazychat.group.service;

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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
}
