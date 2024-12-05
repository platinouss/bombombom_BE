package com.bombombom.devs.external.coupon.service.dto;

import com.bombombom.devs.coupon.model.Coupon;
import lombok.Builder;

@Builder
public record CouponDetailsResult(
    Long id,
    String title,
    String description,
    String rewardType,
    Long rewardValue
) {

    public static CouponDetailsResult fromEntity(Coupon coupon) {
        return CouponDetailsResult.builder()
            .id(coupon.getId())
            .title(coupon.getTitle())
            .description(coupon.getDescription())
            .rewardType(coupon.getRewardType().name())
            .rewardValue(coupon.getRewardValue())
            .build();
    }
}
