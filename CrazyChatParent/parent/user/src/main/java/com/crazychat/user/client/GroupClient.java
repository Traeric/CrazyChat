package com.crazychat.user.client;

import com.crazychat.user.client.impl.GroupClientImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "crazychat-group", fallback = GroupClientImpl.class)
public interface GroupClient {
    @GetMapping("/group/group_num")
    Integer groupNum();
}
