import axios from 'axios';
import {getUser} from "./auth";

const service = axios.create({
    // baseURL: "https://www.easy-mock.com/mock/5c7bca365aff8d30f9ea7cb5/crazychat",
    baseURL: "http://127.0.0.1:1223",
    timeout: 30000,     // 超时时间
    headers: {
		'Authorization-token': "ZW " + getUser().token,    // 定义头信息，每次请求都将头信息带过去
	},
});

export default service;
