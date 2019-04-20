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
            url: `/user/user/chat_record/${user_id}/${friend_id}`,
            method: "get",
        });
    },
    // 发送消息
    sendMessage(type, user_id, other_id, message, removeFlag) {
        return request({
            url: `/chat/chat/${type}/${user_id}/${other_id}`,
            method: "post",
            data: {
                message,
                removeFlag,
            },
        });
    },
    // 获取群聊天记录
    getGroupChatRecord(group_id) {
        return request({
            url: `/chat/chat/group_record/${group_id}`,
            method: "get"
        });
    },
    // 向redis中添加一条未读聊天
    addUnRead(user_id, other_id) {
        return request({
            url: `/chat/chat/add_unread/${user_id}/${other_id}`,
            method: "put",
        });
    },
    // 点击聊天，将redis中保存的未读记录全部删除
    removeUnRead(user_id, other_id) {
        return request({
            url: `/chat/chat/remove_unread/${user_id}/${other_id}`,
            method: "delete",
        });
    },
    // 上传图片
    uploadImage(user_id, dict) {
        return request({
            url: `/chat/chat/upload_image/${user_id}`,
            method: "put",
            data: dict,
        });
    },
};
