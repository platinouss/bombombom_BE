package com.bombombom.devs.external.coupon.service.condition;

import com.bombombom.devs.coupon.enums.CouponConditionType;

public interface CouponConditionValidator {

    CouponConditionType getType();

    boolean canIssueCoupon();
}
