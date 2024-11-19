package com.bombombom.devs.external.coupon.controller;

import com.bombombom.devs.external.coupon.controller.dto.GetCouponsResponse;
import com.bombombom.devs.external.coupon.controller.dto.GetCouponsResponse.CouponDetailsResponse;
import com.bombombom.devs.external.coupon.controller.dto.GetUsableCouponsResponse;
import com.bombombom.devs.external.coupon.controller.dto.GetUsableCouponsResponse.UsableCouponResponse;
import com.bombombom.devs.external.coupon.service.CouponService;
import com.bombombom.devs.external.coupon.service.dto.CouponDetailsResult;
import com.bombombom.devs.external.coupon.service.dto.UsableCouponsResult;
import com.bombombom.devs.external.global.web.LoginUser;
import com.bombombom.devs.security.AppUserDetails;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/coupons")
public class CouponController {

    private final CouponService couponService;

    @GetMapping("")
    public ResponseEntity<GetCouponsResponse> getCoupons() {
        List<CouponDetailsResult> results = couponService.findIssuableCoupons();
        return ResponseEntity.ok(GetCouponsResponse.of(
            results.stream().map(CouponDetailsResponse::fromResult).toList()));
    }

    @GetMapping("/me")
    public ResponseEntity<GetUsableCouponsResponse> getMyCoupons(
        @LoginUser AppUserDetails userDetails) {
        List<UsableCouponsResult> results = couponService.findUsableCoupons(userDetails.getId());
        return ResponseEntity.ok(GetUsableCouponsResponse.of(
            results.stream().map(UsableCouponResponse::fromResult).toList()));
    }

    @PostMapping("/{coupon_id}")
    public ResponseEntity<Void> issueCoupon(@PathVariable("coupon_id") Long couponId,
        @LoginUser AppUserDetails userDetails) {
        couponService.createCoupon(couponId, userDetails.getId());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{coupon_id}/apply")
    public ResponseEntity<Void> applyCoupon(@PathVariable("coupon_id") Long userCouponId,
        @LoginUser AppUserDetails userDetails) {
        couponService.useCoupon(userDetails.getId(), userCouponId);
        return ResponseEntity.ok().build();
    }
}
