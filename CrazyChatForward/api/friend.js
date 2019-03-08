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
};


