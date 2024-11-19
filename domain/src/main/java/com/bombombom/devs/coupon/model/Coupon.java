package com.bombombom.devs.coupon.model;

import com.bombombom.devs.common.BaseEntity;
import com.bombombom.devs.coupon.enums.CouponConditionType;
import com.bombombom.devs.coupon.enums.CouponExpirationType;
import com.bombombom.devs.coupon.enums.CouponRewardType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    @Column(name = "reward_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private CouponRewardType rewardType;

    @Column(name = "reward_value")
    private Long rewardValue;

    @Column(name = "condition_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private CouponConditionType conditionType;

    @Column(name = "condition_value")
    private Long conditionValue;

    @Column(name = "expiration_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private CouponExpirationType expirationType;

    @Column(name = "expiration_value")
    private Long expirationValue;

    @Column(name = "is_issuable")
    private boolean isIssuable = true;

    @OneToMany(mappedBy = "coupon")
    private List<UserCoupon> couponHistories = new ArrayList<>();
}
