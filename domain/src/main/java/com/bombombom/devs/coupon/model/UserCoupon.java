package com.bombombom.devs.coupon.model;

import com.bombombom.devs.common.BaseEntity;
import com.bombombom.devs.coupon.enums.CouponStatus;
import com.bombombom.devs.user.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@Table(name = "user_coupon", uniqueConstraints = {
    @UniqueConstraint(name = "unique_coupon_user", columnNames = {"coupon_id", "user_id"})
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCoupon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false,
        foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Coupon coupon;

    @ManyToOne
    @JoinColumn(nullable = false,
        foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private User user;

    @Setter
    private LocalDate expireAt;

    @Column(nullable = false)
    private CouponStatus couponStatus;

    public void setUsed() {
        this.couponStatus = CouponStatus.USED;
    }

    public static UserCoupon issue(Coupon coupon, User user) {
        return UserCoupon.builder()
            .coupon(coupon)
            .user(user)
            .couponStatus(CouponStatus.NOT_USED)
            .build();
    }
}
