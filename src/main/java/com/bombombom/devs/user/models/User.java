package com.bombombom.devs.user.models;

import com.bombombom.devs.global.audit.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Entity
@Getter
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    private String image;
    private String introduce;
    private String baekjoon;
    private Integer reliability;
    private Integer money;
}
