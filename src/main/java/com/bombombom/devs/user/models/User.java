package com.bombombom.devs.user.models;

import com.bombombom.devs.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
