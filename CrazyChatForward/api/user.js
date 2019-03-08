import request from '../utils/request';

export default {
    // 登录
    login(username, password) {
        return request({
            url: "/user/user/login",
            method: "post",
            data: {
                username,
                password,
            },
        });
    },
    // 用户注册
    register(code, registerData) {
        return request({
            url: `/user/user/register/${code}`,
            method: "post",
            data: registerData,
        });
    },
    // 搜索用户
    searchUser(user_name) {
        return request({
            url: `/friend/friend/search_friend/${user_name}`,
            method: "get",
        });
    },
    // 搜索用户的分组列表
    getGroupList(user_id) {
        return request({
            url: `/friend/friend/get_group_list/${user_id}`,
            method: "get",
        });
    },
}
