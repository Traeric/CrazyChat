import request from '../utils/request';

export default {
    // 登录
    login(email, password) {
        return request({
            url: "/user/user/login",
            method: "post",
            data: {
                email,
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
    searchUser(user_name, user_id) {
        return request({
            url: `/user/user/search_friend/${user_name}/${user_id}`,
            method: "get",
        });
    },
    // 搜索用户的分组列表
    getGroupList(user_id) {
        return request({
            url: `/user/user/get_group_list/${user_id}`,
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
    },
    // 查询分组是否为空
    groupIsEmpty(group_id) {
        return request({
            url: `/user/user/is_empty/${group_id}`,
            method: "get",
        });
    },
    // 删除用户分组
    deleteGroup(group_id) {
        return request({
            url: `/user/user/delete_group/${group_id}`,
            method: "delete",
        });
    },
    // 新增分组
    addUserGroup(user_id, group_name) {
        return request({
            url: `/user/user/add_group/${user_id}`,
            method: "post",
            data: {
                "name": group_name,
            },
        });
    },
    // 修改好友分组
    changeUserGroup(user_id, old_group_id, new_group_id) {
        return request({
            url: `/user/user/change_group/${user_id}/${old_group_id}/${new_group_id}`,
            method: "put",
        });
    },
    // 获取验证码
    getConfirmCode(email) {
        return request({
            url: `/user/user/send_email`,
            method: "post",
            data: {
                email,
            },
        });
    },
    // 从redis中删除验证信息
    removeConfirmInfo(user_id, other_id, type) {
        return request({
            url: `/user/user/remove_confirm_info/${user_id}/${other_id}/${type}`,
            method: "delete",
        });
    },
    // 同意好友加好友申请
    allowFriendApply(user_id, friend_id) {
        return request({
            url: `/user/user/allow_friend_apply/${user_id}/${friend_id}`,
            method: "post",
        });
    },
    // 加载验证消息
    loadVerify(user_id) {
        return request({
            url: `/user/user/load_verify/${user_id}`,
            method: "get",
        });
    },
	
	// 加载通知
	loadNotice(user_id) {
		return request({
			url: `/user/admin/load_notice/${user_id}`,
			method: "get",
		});
	},
	
	// 将公告标为已读
	readNotice(noticeKey) {
		return request({
			url: '/user/admin/read_notice',
			method: "put",
			data: {
				noticeKey,
			},
		});
	},
	
	// 将公告标为未读
	noReadNotice(noticeKey) {
		return request({
			url: '/user/admin/no_read_notice',
			method: "put",
			data: {
				noticeKey,
			},
		});
	},
	
	// 删除公告
	deleteNotice(noticeKey) {
		return request({
			url: "/user/admin/delete_notice",
			method: "put",
			data: {
				noticeKey,
			},
		});
	},
	
	// 是否有未读的系统消息
	haveNoReadNotice(user_id) {
		return request({
			url: `/user/admin/have_no_read_notice/${user_id}`,
			method: "get",
		});
	},
}
