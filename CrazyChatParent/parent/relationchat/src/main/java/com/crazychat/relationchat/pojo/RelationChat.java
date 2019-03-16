package com.crazychat.relationchat.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

@Data
public class RelationChat implements Serializable {
    @Id
    private String _id;

    private String userId;

    private String otherId;

    private String type;
}
