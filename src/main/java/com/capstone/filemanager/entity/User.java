package com.capstone.filemanager.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="users")
public class User extends BaseEntity {
    @Getter
    @Column(unique = true, nullable = false)
    private String username;

    @Setter
    @Column(nullable = false)
    private String password;

    protected User(){}

    public User(String username, String password){
        this.username = username;
        this.password = password;
    }

}
