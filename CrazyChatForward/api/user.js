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
    // 获取用户信息
    getUserInfo(user_id) {
        return request({
            url: `/user/user/user_info/${user_id}`,
            method: "get",
        });
    },
    // 修改用户昵称
    profileNick(user_id, nick) {
        return request({
            url: `/user/user/profile_nick/${user_id}`,
            method: "put",
            data: {
                "nick": nick,
            },
        });
    },
    // 修改用户邮箱
    profileEmail(user_id, new_email) {
        return request({
            url: `/user/user/profile_email/${user_id}`,
            method: "put",
            data: {
                "email": new_email,
            },
        });
    },
    // 修改用户其他设置
    profileInfo(user_id, info) {
        return request({
            url: `/user/user/profile_info/${user_id}`,
            method: "put",
            data: info,
        });
    },
    // 修改用户头像
    profileAvatar(user_id, dict) {
        return request({
            url: `/user/user/profile_avatar/${user_id}`,
            method: "put",
            data: dict,
        });
    }
}
