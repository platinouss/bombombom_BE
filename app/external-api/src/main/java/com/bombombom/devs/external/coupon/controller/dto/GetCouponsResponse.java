package com.bombombom.devs.external.coupon.controller.dto;

import com.bombombom.devs.external.coupon.service.dto.CouponDetailsResult;
import java.util.List;
import lombok.Builder;

@Builder
public record GetCouponsResponse(
    List<CouponDetailsResponse> coupons
) {

    public static GetCouponsResponse of(List<CouponDetailsResponse> response) {
        return GetCouponsResponse.builder()
            .coupons(response)
            .build();
    }

    @Builder
    public record CouponDetailsResponse(
        Long id,
        String title,
        String description,
        String rewardType,
        Long rewardValue
    ) {

        public static CouponDetailsResponse fromResult(CouponDetailsResult result) {
            return CouponDetailsResponse.builder()
                .id(result.id())
                .title(result.title())
                .description(result.description())
                .rewardType(result.rewardType())
                .rewardValue(result.rewardValue())
                .build();
        }
    }
}

