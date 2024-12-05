package com.bombombom.devs.external.coupon.controller.dto;

import com.bombombom.devs.external.coupon.service.dto.UsableCouponsResult;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

@Builder
public record GetUsableCouponsResponse(
    List<UsableCouponResponse> coupons
) {

    public static GetUsableCouponsResponse of(List<UsableCouponResponse> response) {
        return GetUsableCouponsResponse.builder()
            .coupons(response)
            .build();
    }

    @Builder
    public record UsableCouponResponse(
        Long id,
        String title,
        String description,
        String rewardType,
        Long rewardValue,
        LocalDate expireAt
    ) {

        public static UsableCouponResponse fromResult(UsableCouponsResult result) {
            return UsableCouponResponse.builder()
                .id(result.userCouponId())
                .title(result.title())
                .description(result.description())
                .rewardType(result.rewardType().name())
                .rewardValue(result.rewardValue())
                .expireAt(result.expireAt())
                .build();
        }
    }
}
