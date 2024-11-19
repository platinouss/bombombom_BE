package com.bombombom.devs.external.coupon.service.expiration;

import com.bombombom.devs.coupon.enums.CouponExpirationType;
import com.bombombom.devs.coupon.model.UserCoupon;

public interface CouponExpirationStrategy {

    CouponExpirationType getType();

    void setExpiration(UserCoupon userCoupon);
}
