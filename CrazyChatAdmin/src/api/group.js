import request from "../utils/request";


export default {
    // 获取所有的群聊
    findAllGroup(page) {
        return request({
            url: `/group/group/find_all/${page}`,
            method: "get",
        });
    },
    // 通过名字筛选群聊名称
    searchByName(name, page) {
        return request({
            url: `/group/group/search_by_name/${name}/${page}`,
            method: "get",
        });
    },
    // 删除群聊
    deleteGroup(group_id) {
        return request({
            url: `/group/group/delete_group_admin/${group_id}`,
            method: "delete",
        });
    },
    // 获取所有的群成员
    getAllMembers(group_id) {
        return request({
            url: `/group/group/get_members/${group_id}`,
            method: 'get',
        });
    },
    // 获取用户的群组
    getGroupByUser(user_id) {
        return request({
            url: `/group/group/group_list/${user_id}`,
            method: "get",
        });
    },
};
