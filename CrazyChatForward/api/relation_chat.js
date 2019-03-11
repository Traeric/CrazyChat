import request from "../utils/request";

export default {
    // 删除最近联系人
    deleteRelationChat(chat_id) {
        return request({
            url: `/relationchat/relationchat/delete_chat/${chat_id}`,
            method: 'delete',
        });
    },
    // 添加最近联系人
    addRelationChat(user_id, chat_id) {
        return request({
            url: `/relationchat/relationchat/add_chat/${user_id}/${chat_id}`,
            method: "post",
        });
    },
}
