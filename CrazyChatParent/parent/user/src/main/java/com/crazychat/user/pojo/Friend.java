package com.crazychat.user.pojo;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@Table(name = "friend")
public class Friend implements Serializable {
    @Id
    private String id;

    private String friendgroupId;

    private String friendId;

    private String todo;

    private String userId;
}
