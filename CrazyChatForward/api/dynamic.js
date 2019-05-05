import request from '../utils/request';


export default {
    // 添加动态
    addDynamic(dynamic_data) {
        return request({
            url: "/dynamic/dynamic/add_dynamic",
            method: "post",
            data: dynamic_data,
        });
    },
    // 获取个人动态
    loadDynamicPerson(user_id) {
        return request({
            url: `/dynamic/dynamic/load_dynamic_per/${user_id}`,
            method: "get"
        });
    },
};

