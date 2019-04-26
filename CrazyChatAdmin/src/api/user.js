import request from "../utils/request";

export default {
    // 登录验证
    login(username, password) {
        return request({
            url: "/user/admin/admin_login",
            method: "post",
            data: {
                username,
                password,
            },
        });
    },
    // 登录的用户数以及注册用户数
    UserNum() {
        return request({
            url: "/user/admin/user_num",
            method: "get",
        });
    },
    // 获取所有的用户
    findAllUser(page) {
        return request({
            url: `/user/user/find_all/${page}`,
            method: "get",
        });
    },
    // 获取所有的用户，不分页
    findAll() {
        return request({
            url: '/user/user/find_all',
            method: "get",
        });
    },
    // 通过状态筛选用户
    searchUserByStatus(status, page) {
        return request({
            url: `/user/user/search_by_status/${status}/${page}`,
            method: "get",
        });
    },
    // 通过名称筛选用户
    searchByName(name, page) {
        return request({
            url: `/user/user/search_by_name/${name}/${page}`,
            method: "get",
        });
    },
    // 通过名称和状态筛选用户
    searchByNameAndStatus(name, status, page) {
        return request({
            url: `user/user/search_by_name_and_status/${name}/${status}/${page}`,
            method: "get",
        });
    },
	// 修改状态
    changeStatus(user_id, status) {
        return request({
            url: `/user/user/change_status/${user_id}/${status}`,
            method: "put",
        });
    },
    // 删除账号
    deleteUser(user_id) {
        return request({
            url: `/user/user/delete_account/${user_id}`,
            method: "delete",
        });
    },
    // 给所有人推送通告
    sendNoticeToAll(message) {
        return request({
            url: `/user/admin/send_nitice_to_all`,
            method: "post",
            data: {
                message,
            },
        });
    },
    // 推送给指定用户
    sendNoticeToSelect(message, selectList) {
        return request({
            url: '/user/admin/send_notice_to_select',
            method: "post",
            data: {
                message,
                selectList,
            },
        });
    },
	// 修改密码
	modifyPwassord(adminId, password) {
		return request({
			url: `/user/admin/modify_password/${adminId}`,
			method: "put",
			data: {
				password,
			},
		});
	},
};


