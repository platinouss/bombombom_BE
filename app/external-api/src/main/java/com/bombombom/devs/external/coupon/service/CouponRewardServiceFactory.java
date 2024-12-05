package com.bombombom.devs.external.coupon.service;

import com.bombombom.devs.coupon.enums.CouponRewardType;
import com.bombombom.devs.external.coupon.service.reward.CouponRewardService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class CouponRewardServiceFactory {

    private final Map<CouponRewardType, CouponRewardService> couponUsageServiceMap = new HashMap<>();

    public CouponRewardServiceFactory(List<CouponRewardService> usages) {
        usages.forEach(
            usage -> couponUsageServiceMap.put(usage.getType(), usage));
    }

    public CouponRewardService getService(CouponRewardType rewardType) {
        return couponUsageServiceMap.get(rewardType);
    }
}
