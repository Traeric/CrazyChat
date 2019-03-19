package com.crazychat.user.pojo;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Data
@Entity
@Table(name = "friend_group")
public class FriendGroup implements Serializable {
    @Id
    private String id;

    private String name;

    private String userId;


    @Override
    public String toString() {
        return "FriendGroup{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
