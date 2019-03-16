package com.crazychat.chat.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

@Data
public class ChatRecord implements Serializable {
    @Id
    private String _id;

    private String userId;

    private String friendId;

    private String content;

    private String status;   // 状态 left or right
}
