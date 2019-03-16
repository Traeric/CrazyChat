package com.crazychat.group.pojo;

import lombok.Data;

import javax.persistence.Id;
import java.io.Serializable;

@Data
public class ChatRecord implements Serializable {
    @Id
    private String _id;

    private String groupId;

    private String userId;

    private String content;

    private String status;
}
