package com.crazychat.relationchat.service;

import com.crazychat.common.utils.IdWorker;
import com.crazychat.relationchat.client.ChatClient;
import com.crazychat.relationchat.client.GroupClient;
import com.crazychat.relationchat.client.UserClient;
import com.crazychat.relationchat.dao.RelationChatDao;
import com.crazychat.relationchat.pojo.RelationChat;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class RelationChatService {
    @Resource
    private RelationChatDao relationChatDao;

    @Resource
    private UserClient userClient;

    @Resource
    private GroupClient groupClient;

    @Resource
    private ChatClient chatClient;

    @Resource(name = "idWorker")
    private IdWorker idCreater;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 获取最近聊天的列表
     * @param userId
     * @return
     */
    public List<Map<String, Object>> getRelatioChatList(String userId) {
        // 根据用户名查询所有的最近聊天记录
        List<RelationChat> relationChats = relationChatDao.findAllByUserId(userId);
        // 返回的数据列表
        List<Map<String, Object>> data = new LinkedList<>();
        relationChats.parallelStream().forEach((relationChat) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", relationChat.getOtherId());
            if ("0".equals(relationChat.getType())) {
                // 用户
                String todo = new String(userClient.getUserTodo(userId, relationChat.getOtherId()));
                map.put("name", todo);
                String avatar = new String(userClient.getUserAvatarById(relationChat.getOtherId()));
                map.put("picture", avatar);
                Map<String, String> map1 = new HashMap<>();
                map1.put("type", relationChat.getType());
                map.put("data", map1);
                // 获取两个用户之间最后一次聊天
                String message = chatClient.lastMsgUser2User(relationChat.getUserId(), relationChat.getOtherId());
                map.put("lastMsg", message);
            } else {
                // 群
                String groupName = groupClient.getGroupNameById(relationChat.getOtherId());
                map.put("name", groupName);
                String picture = groupClient.getGroupPictureById(relationChat.getOtherId());
                map.put("picture", picture);
                // 返回的data数据
                Map<String, String> map1 = new HashMap<>();
                map1.put("type", relationChat.getType());
                // 最后一条数据
                Map<String, String> msg = chatClient.getLastMessage(relationChat.getOtherId());
                map1.put("user", msg.get("user"));
                map.put("data", map1);
                map.put("lastMsg", msg.get("msg"));
            }
            // 从redis中获取未读取的聊天
            String key = userId + "|" + relationChat.getOtherId();
            Object unRead = redisTemplate.opsForValue().get(key);
            if (null == unRead) {
                map.put("unRead", "");
            } else {
                map.put("unRead", unRead);
            }
            data.add(map);
        });
        return data;
    }

    /**
     * 添加最近联系人
     * @param userId
     * @param otherId
     * @param type
     */
    public void addRelationChat(String userId, String otherId, String type) {
        RelationChat relationChat = new RelationChat();
        relationChat.set_id(String.valueOf(idCreater.nextId()));
        relationChat.setUserId(userId);
        relationChat.setOtherId(otherId);
        relationChat.setType(type);
        relationChatDao.save(relationChat);
    }

    /**
     * 删除最近联系人
     * @param userId
     * @param otherId
     */
    public void deleteRelationChat(String userId, String otherId) {
        RelationChat relationChat = relationChatDao.findByUserIdAndOtherId(userId, otherId);
        if (null != relationChat) {
            relationChatDao.delete(relationChat);
        }
    }
}
