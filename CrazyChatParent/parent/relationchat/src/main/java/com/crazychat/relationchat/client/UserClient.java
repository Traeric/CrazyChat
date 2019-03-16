package com.crazychat.relationchat.client;

import com.crazychat.relationchat.client.impl.UserClientImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "crazychat-user", fallback = UserClientImpl.class)
public interface UserClient {
    /**
     * 获取用户的名称
     * @param userId
     * @return
     */
    @GetMapping("/user/name/{user_id}")
    String getUserNameById(@PathVariable("user_id") String userId);

    /**
     * 获取用户的头像
     * @param userId
     * @return
     */
    @GetMapping("/user/user_id/{user_id}")
    String getUserAvatarById(@PathVariable("user_id") String userId);
}
