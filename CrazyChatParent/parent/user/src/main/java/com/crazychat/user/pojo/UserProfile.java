package com.crazychat.user.pojo;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name = "userprofile")
public class UserProfile implements Serializable {
    @Id
    private String id;

    private String name;

    private String email;

    private String sign;

    private Date birthday;

    private String gender;

    private String userdescribe;

    private String avatar = "/_nuxt/assets/images/avtar.png";

    private String password;

    private Integer status;

    @Override
    public String toString() {
        return "UserProfile{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", sign='" + sign + '\'' +
                ", birthday=" + birthday +
                ", gender='" + gender + '\'' +
                ", userdescribe='" + userdescribe + '\'' +
                ", avatar='" + avatar + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
