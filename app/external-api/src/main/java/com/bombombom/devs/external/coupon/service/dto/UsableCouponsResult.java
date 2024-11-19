package com.bombombom.devs.external.coupon.service.dto;

import com.bombombom.devs.coupon.enums.CouponRewardType;
import com.bombombom.devs.coupon.model.Coupon;
import com.bombombom.devs.coupon.model.UserCoupon;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record UsableCouponsResult(
    Long userCouponId,
    String title,
    String description,
    CouponRewardType rewardType,
    Long rewardValue,
    LocalDate expireAt
) {

    public static UsableCouponsResult fromEntity(UserCoupon userCoupon) {
        Coupon coupon = userCoupon.getCoupon();
        return UsableCouponsResult.builder()
            .userCouponId(userCoupon.getId())
            .title(coupon.getTitle())
            .description(coupon.getDescription())
            .rewardType(coupon.getRewardType())
            .rewardValue(coupon.getRewardValue())
            .expireAt(userCoupon.getExpireAt())
            .build();
    }
}
