package com.bombombom.devs.external.coupon.service;

import com.bombombom.devs.core.exception.BusinessRuleException;
import com.bombombom.devs.core.exception.ErrorCode;
import com.bombombom.devs.core.exception.NotFoundException;
import com.bombombom.devs.coupon.enums.CouponStatus;
import com.bombombom.devs.coupon.model.Coupon;
import com.bombombom.devs.coupon.model.UserCoupon;
import com.bombombom.devs.coupon.repository.CouponRepository;
import com.bombombom.devs.coupon.repository.UserCouponRepository;
import com.bombombom.devs.external.coupon.service.dto.CouponDetailsResult;
import com.bombombom.devs.external.coupon.service.dto.UsableCouponsResult;
import com.bombombom.devs.user.model.User;
import com.bombombom.devs.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final UserRepository userRepository;
    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;
    private final CouponRewardServiceFactory couponRewardServiceFactory;
    private final CouponConditionValidatorFactory couponConditionValidatorFactory;
    private final CouponExpirationStrategyFactory couponExpirationStrategyFactory;

    @Transactional(readOnly = true)
    public List<CouponDetailsResult> findIssuableCoupons() {
        List<Coupon> coupons = couponRepository.findAll().stream()
            .filter(Coupon::isIssuable).toList();
        return coupons.stream().map(CouponDetailsResult::fromEntity).toList();
    }

    @Transactional(readOnly = true)
    public List<UsableCouponsResult> findUsableCoupons(Long userId) {
        List<UserCoupon> userCoupons = userCouponRepository.findUserCouponWithCouponByUser(userId)
            .stream()
            .filter(userCoupon -> userCoupon.getCouponStatus() == CouponStatus.NOT_USED)
            .toList();
        return userCoupons.stream().map(UsableCouponsResult::fromEntity).toList();
    }

    @Transactional
    public void createCoupon(Long couponId, Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
        Coupon coupon = couponRepository.findById(couponId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.COUPON_NOT_FOUND));
        if (!coupon.isIssuable()) {
            throw new NotFoundException(ErrorCode.COUPON_NOT_FOUND);
        }
        if (!couponConditionValidatorFactory.getValidator(coupon.getConditionType())
            .canIssueCoupon()) {
            throw new BusinessRuleException(ErrorCode.COUPON_CONDITION_NOT_FULFILLED);
        }
        UserCoupon userCoupon = UserCoupon.issue(coupon, user);
        couponExpirationStrategyFactory.getStrategy(coupon.getExpirationType())
            .setExpiration(userCoupon);
        try {
            userCouponRepository.save(userCoupon);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessRuleException(ErrorCode.COUPON_ALREADY_ISSUED);
        }
    }

    @Transactional
    public void useCoupon(Long userId, Long userCouponId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
        UserCoupon userCoupon = userCouponRepository.findByCouponAndUserForUpdate(userCouponId,
            userId).orElseThrow(() -> new NotFoundException(ErrorCode.COUPON_NOT_FOUND));
        if (userCoupon.getCouponStatus() != CouponStatus.NOT_USED) {
            throw new BusinessRuleException(ErrorCode.COUPON_ALREADY_USED_OR_EXPIRED);
        }
        couponRewardServiceFactory.getService(userCoupon.getCoupon().getRewardType())
            .use(user, userCoupon);
        userCoupon.setUsed();
    }
}
