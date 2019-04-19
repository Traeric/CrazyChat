package com.crazychat.user.client.impl;

import com.crazychat.common.entity.Result;
import com.crazychat.common.entity.StatusCode;
import com.crazychat.user.client.RelationChatClient;

public class RelationChatClientImpl implements RelationChatClient {

    @Override
    public Result deleteRelationChat(String userId, String otherId) {
        return new Result(false, StatusCode.ERROR.getCode(), "删除错误");
    }
}
