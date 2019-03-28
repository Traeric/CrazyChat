package com.crazychat.chat.client.impl;

import com.crazychat.chat.client.RelationChatClient;
import com.crazychat.common.entity.Result;
import com.crazychat.common.entity.StatusCode;

public class RelationChatClientImpl implements RelationChatClient {

    @Override
    public boolean inRelationChat(String userId, String otherId) {
        return false;
    }

    @Override
    public Result deleteRelationChat(String userId, String otherId) {
        return new Result(false, StatusCode.REMOTEERROR.getCode(), "远程调用失败");
    }

    @Override
    public void addRealtionChat(String userId, String otherId, String type) {
    }
}
