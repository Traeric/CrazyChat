import request from "../utils/request";


export default {
	// 用对用户的聊天记录查询
	user2user(from, to, startTime,endTime) {
		return request({
			url: `/chat/chat/admin_get_user_record/${from}/${to}/${startTime}/${endTime}`,
			method: "get",
		});
	},
	// 查询群聊的查询
	user2groupAll(user, group, startTime, endTime) {
		return request({
			url: `/chat/chat/admin_get_group_record_all/${user}/${group}/${startTime}/${endTime}`,
			method: "get",
		});
	},
}


