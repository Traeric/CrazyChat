import axios from "axios";
import {getUser} from "./auth";

// 创建axios实例
const service = axios.create({
    baseURL: "https://www.easy-mock.com/mock/5c7bca365aff8d30f9ea7cb5/crazychat",
    // baseURL: "http://127.0.0.1:4140",
    timeout: 30000,     // 超时时间
    headers: {
        'Authorization': "ZW " + getUser().token,   // 每次请求都带上该头信息
        'withCredentials': true,
        'Access-Control-Allow-Origin': "*",
    },
});

export default service;
