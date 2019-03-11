import request from "../utils/request";

export default {
    // 添加朋友
    addFriend(user_id, friend_id, addFriendMap) {
        return request({
            url: `/friend/friend/add_friend/${user_id}/${friend_id}`,
            method: "post",
            data: addFriendMap,
        });
    },
    // 删除好友
    deleteFriend(user_id, friend_id) {
        return request({
            url: `/friend/friend/delete_friend/${user_id}/${friend_id}`,
            method: "delete",
        });
    },
    // 修改备注
    changeTod(user_id, friend_id, todo) {
        return request({
            url: `/friend/friend/change_todo/${user_id}/${friend_id}`,
            method: "put",
            data: {
                todo,
            },
        });
    },
};


