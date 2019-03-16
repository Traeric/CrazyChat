package com.crazychat.group.pojo;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "group_chat")
public class Group implements Serializable {
    @Id
    private String id;

    private String name;

    private Date createTime;

    private String picture;     // 群图片

    @Override
    public String toString() {
        return "Group{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}
