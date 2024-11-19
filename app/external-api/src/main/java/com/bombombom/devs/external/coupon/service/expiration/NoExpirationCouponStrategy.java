package com.bombombom.devs.external.coupon.service.expiration;

import com.bombombom.devs.coupon.enums.CouponExpirationType;
import com.bombombom.devs.coupon.model.UserCoupon;
import org.springframework.stereotype.Service;

@Service
public class NoExpirationCouponStrategy implements CouponExpirationStrategy {

    @Override
    public CouponExpirationType getType() {
        return CouponExpirationType.NONE;
    }

    @Override
    public void setExpiration(UserCoupon userCoupon) {
        userCoupon.setExpireAt(null);
    }
}
