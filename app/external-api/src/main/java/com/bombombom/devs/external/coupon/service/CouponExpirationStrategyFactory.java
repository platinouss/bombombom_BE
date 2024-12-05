package com.bombombom.devs.external.coupon.service;

import com.bombombom.devs.coupon.enums.CouponExpirationType;
import com.bombombom.devs.external.coupon.service.expiration.CouponExpirationStrategy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class CouponExpirationStrategyFactory {

    private final Map<CouponExpirationType, CouponExpirationStrategy> couponExpirationStrategyMap = new HashMap<>();

    public CouponExpirationStrategyFactory(List<CouponExpirationStrategy> strategies) {
        strategies.forEach(
            strategy -> couponExpirationStrategyMap.put(strategy.getType(), strategy));
    }

    public CouponExpirationStrategy getStrategy(CouponExpirationType expirationType) {
        return couponExpirationStrategyMap.get(expirationType);
    }
}
