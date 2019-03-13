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

    /**
     * 多对多
     */
    @ManyToMany
    @JoinTable(name = "userprofile_friendgroup",    // 中间表
            // 指定当前表在中间表的外键名称，和当前标的主键
            joinColumns = {@JoinColumn(name = "friendgroup_id", referencedColumnName = "id")},
            // 另一张表的主外键关联关系
            inverseJoinColumns = {@JoinColumn(name = "userprofile_id", referencedColumnName = "id")}
    )
    private Set<UserProfile> friends;

    @Override
    public String toString() {
        return "FriendGroup{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
