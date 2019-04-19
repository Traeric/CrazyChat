package com.crazychat.chat.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.Date;

@Data
public class ChatRecord implements Serializable {
    @Id
    private String _id;

    private String userId;

    private String friendId;

    private String content;

    private String status;   // 状态 left or right

    private String createTime;   // 创建时间

    @Override
    public String toString() {
        return "ChatRecord{" +
                "content='" + content + '\'' +
                '}';
    }
}
