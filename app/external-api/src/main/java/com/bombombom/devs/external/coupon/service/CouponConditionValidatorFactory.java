package com.bombombom.devs.external.coupon.service;

import com.bombombom.devs.coupon.enums.CouponConditionType;
import com.bombombom.devs.external.coupon.service.condition.CouponConditionValidator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class CouponConditionValidatorFactory {

    private final Map<CouponConditionType, CouponConditionValidator> couponConditionValidatorMap = new HashMap<>();

    public CouponConditionValidatorFactory(List<CouponConditionValidator> validators) {
        validators.forEach(
            validator -> couponConditionValidatorMap.put(validator.getType(), validator));
    }

    public CouponConditionValidator getValidator(CouponConditionType conditionType) {
        return couponConditionValidatorMap.get(conditionType);
    }
}
