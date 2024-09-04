package com.bombombom.devs.user.model;

import com.bombombom.devs.common.BaseEntity;
import com.bombombom.devs.core.exception.BusinessRuleException;
import com.bombombom.devs.core.exception.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    @Column(nullable = false)
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
            throw new BusinessRuleException(ErrorCode.NEGATIVE_AMOUNT);
        }
        if (this.money < money) {
            throw new BusinessRuleException(ErrorCode.NOT_ENOUGH_MONEY);
        }
        this.money -= money;
    }
}
