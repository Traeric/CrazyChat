import request from '../utils/request';
import user from "./user";


export default {
    // 获取用户的群组列表，最近聊天的列表
    getRelationChatList(user_id) {
        return request({
            url: `/relationchat/relationchat/relation_chat/${user_id}`,
            method: "get",
        });
    },
    // 获取用户的好友列表
    getUserFriendList(user_id) {
        return request({
            url: `/user/user/friend_list/${user_id}`,
            method: "get",
        });
    },
    // 获取用户的群组列表
    getUserGroupList(user_id) {
        return request({
            url: `/group/group/group_list/${user_id}`,
            method: "get",
        });
    },
    // 查询与用户的聊天记录
    getChatRecord(user_id, friend_id) {
        return request({
            url: `/chat/chat/chat_record/${user_id}/${friend_id}`,
            method: "get",
        });
    },
    // 发送消息
    sendMessage(type, user_id, other_id, message) {
        return request({
            url: `/chat/chat/${type}/${user_id}/${other_id}`,
            method: "post",
            data: {
                message,
            },
        });
    },
    // 获取群聊天记录
    getGroupChatRecord(group_id) {
        return request({
            url: `/chat/chat/group_recore/${group_id}`,
            method: "get"
        });
    },
};
