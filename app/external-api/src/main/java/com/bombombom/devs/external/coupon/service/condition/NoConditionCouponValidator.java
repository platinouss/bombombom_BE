package com.bombombom.devs.external.coupon.service.condition;

import com.bombombom.devs.coupon.enums.CouponConditionType;
import org.springframework.stereotype.Service;

@Service
public class NoConditionCouponValidator implements CouponConditionValidator {

    @Override
    public CouponConditionType getType() {
        return CouponConditionType.NONE;
    }

    @Override
    public boolean canIssueCoupon() {
        return true;
    }
}
