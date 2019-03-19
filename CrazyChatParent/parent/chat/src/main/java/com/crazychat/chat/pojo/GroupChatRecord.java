package com.crazychat.chat.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

@Data
public class GroupChatRecord implements Serializable {
    @Id
    private String _id;

    private String groupId;

    private String userId;

    private String content;

    // left or right
    private String status;
}
