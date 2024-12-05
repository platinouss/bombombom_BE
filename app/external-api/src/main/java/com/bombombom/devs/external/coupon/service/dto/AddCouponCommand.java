package com.bombombom.devs.external.coupon.service.dto;

import lombok.Builder;

@Builder
public record AddCouponCommand(
    Long userId,
    Long couponId
) {

}
