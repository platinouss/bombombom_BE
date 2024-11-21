package com.bombombom.devs.external.coupon.service.reward;

import com.bombombom.devs.coupon.enums.CouponRewardType;
import com.bombombom.devs.coupon.model.Coupon;
import com.bombombom.devs.coupon.model.UserCoupon;
import com.bombombom.devs.external.points.service.PointsService;
import com.bombombom.devs.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointRewardCouponService implements CouponRewardService {

    private final PointsService pointsService;

    @Override
    public CouponRewardType getType() {
        return CouponRewardType.POINT_REWARD;
    }

    @Override
    public void use(User user, UserCoupon userCoupon) {
        Coupon coupon = userCoupon.getCoupon();
        pointsService.updateUserPoints(user, coupon.getRewardValue(), coupon.getTitle());
    }
}
