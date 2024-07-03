package com.bombombom.devs.user.models;

import com.bombombom.devs.global.audit.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Role role;

    private String image;
    private String introduce;
    private String baekjoon;
    private Integer reliability;
    private Integer money;

    public static User signup(String username, String password, String introduce) {
        return User.builder()
            .username(username)
            .password(password)
            .introduce(introduce)
            .role(Role.USER)
            .reliability(0)
            .money(0)
            .build();
    }
    
    public void payMoney(Integer money) {
        if (money < 0) {
            throw new IllegalStateException("Money must be positive");
        }
        if (this.money < money) {
            throw new IllegalStateException("User have Not enough money");
        }
        this.money -= money;
    }
}
