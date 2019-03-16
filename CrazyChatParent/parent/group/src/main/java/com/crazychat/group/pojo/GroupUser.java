package com.crazychat.group.pojo;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "group_userprofile")
public class GroupUser {
    @Id
    private String id;

    private String userId;

    private String groupId;

    private String type;

    @Override
    public String toString() {
        return "GroupUser{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", groupId='" + groupId + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
