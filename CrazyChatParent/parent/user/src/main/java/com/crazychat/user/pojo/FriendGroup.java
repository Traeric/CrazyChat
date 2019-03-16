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

    @Transient
    private String userId;

    /**
     * 多对一
     */
    @ManyToOne(targetEntity = UserProfile.class)
    @JoinColumn(name = "userId")
    private UserProfile userProfile;


    @Override
    public String toString() {
        return "FriendGroup{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
