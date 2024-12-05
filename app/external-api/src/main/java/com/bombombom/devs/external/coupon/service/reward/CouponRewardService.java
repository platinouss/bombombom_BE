package com.bombombom.devs.external.coupon.service.reward;

import com.bombombom.devs.coupon.enums.CouponRewardType;
import com.bombombom.devs.coupon.model.UserCoupon;
import com.bombombom.devs.user.model.User;

public interface CouponRewardService {

    CouponRewardType getType();

    void use(User user, UserCoupon userCoupon);
}
