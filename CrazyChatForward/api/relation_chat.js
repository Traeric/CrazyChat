import request from "../utils/request";

export default {
    // 删除最近联系人
    deleteRelationChat(chat_id) {
        return request({
            url: `/relationchat/relationchat/delete_chat/${chat_id}`,
            method: 'delete',
        });
    },
}
