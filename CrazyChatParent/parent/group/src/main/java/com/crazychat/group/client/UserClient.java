package com.crazychat.group.client;

import com.crazychat.group.client.impl.UserClientImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "crazychat-user", fallback = UserClientImpl.class)
public interface UserClient {

    @GetMapping("/user/name/{user_id}")
    byte[] getUserNameById(@PathVariable("user_id") String userId);

    @GetMapping("/user/user_id/{user_id}")
    byte[] getUserAvatarById(@PathVariable("user_id") String userId);
}
