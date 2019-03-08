import request from "../utils/request";

export default {
    // 通过名称搜索群聊
    searchGroupByName(group_name) {
        return request({
            url: `/group/group/search_group/${group_name}`,
            method: "get",
        });
    },
    // 添加群聊
    addGroup(user_id, group_id, confirmInfo) {
        return request({
            url: `/group/group/add_group/${user_id}/${group_id}`,
            method: "post",
            data: {
                confirmInfo,
            },
        });
    },
}

