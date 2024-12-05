package com.bombombom.devs.external.coupon.controller.dto;

import com.bombombom.devs.external.coupon.service.dto.AddCouponCommand;

public record AddCouponRequest(
    Long userId,
    Long couponId
) {

    public AddCouponCommand toServiceDto() {
        return AddCouponCommand.builder()
            .userId(userId)
            .couponId(couponId)
            .build();
    }
}
