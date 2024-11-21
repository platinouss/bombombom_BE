package com.bombombom.devs.points.model;

import com.bombombom.devs.common.BaseEntity;
import com.bombombom.devs.user.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@Table(name = "points_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointsHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false,
        foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private User user;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    private Long total;

    private String contents;

    public static PointsHistory init(User user) {
        return PointsHistory.builder()
            .user(user)
            .amount(0L)
            .total(0L)
            .contents("회원가입")
            .build();
    }

    public static PointsHistory createUpdatePointsHistory(User user, Long prevPoint,
        Long amount, String contents) {
        return PointsHistory.builder()
            .user(user)
            .amount(amount)
            .total(prevPoint + amount)
            .contents(contents)
            .build();
    }
}
