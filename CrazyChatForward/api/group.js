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
    // 创建群聊
    createGroup(groupMap) {
        return request({
            url: "/group/group/create_group",
            method: "post",
            data: groupMap,
        });
    },
    // 查询群聊信息
    getGroupInfo(group_id) {
        return request({
            url: `/group/group/group_info/${group_id}`,
            method: "get"
        });
    },
    // 获取群成员
    getGroupMembers(group_id) {
        return request({
            url: `/group/group/get_members/${group_id}`,
            method: "get",
        });
    },
    // 移除群成员
    removeGroupMember(group_id, member_id) {
        return request({
            url: `/group/group/delete_member/${group_id}/${member_id}`,
            method: "delete",
        });
    },
    // 解散群聊
    deleteGroup(group_id) {
        return request({
            url: `/group/group/delete_group/${group_id}`,
            method: "delete",
        });
    },
}

