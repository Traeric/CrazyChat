package com.crazychat.user.pojo;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "admin_user")
public class AdminUser {
    @Id
    private Integer id;

    private String username;

    private String password;
}
