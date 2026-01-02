package com.wsw.campushelp.entity;

import jakarta.persistence.*; // 注意这里变了
import lombok.Data;

@Data
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;

    // 角色: ADMIN 或 USER
    private String role;

    // 积分，默认100
    private Integer points;
}