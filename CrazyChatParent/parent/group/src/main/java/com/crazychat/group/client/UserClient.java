package com.crazychat.group.client;

import com.crazychat.group.client.impl.UserClientImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "crazychat-user", fallback = UserClientImpl.class)
public interface UserClient {

    @GetMapping("/user/name/{user_id}")
    String getUserNameById(@PathVariable("user_id") String userId);
}
