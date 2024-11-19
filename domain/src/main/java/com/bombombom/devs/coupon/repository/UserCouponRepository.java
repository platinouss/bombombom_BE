package com.bombombom.devs.coupon.repository;

import com.bombombom.devs.coupon.model.UserCoupon;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT uc FROM UserCoupon uc WHERE uc.id = :userCouponId AND uc.user.id = :userId")
    Optional<UserCoupon> findByCouponAndUserForUpdate(Long userCouponId, Long userId);

    @Query("SELECT uc FROM UserCoupon uc "
        + "JOIN FETCH uc.coupon c "
        + "WHERE uc.user.id = :userId")
    List<UserCoupon> findUserCouponWithCouponByUser(Long userId);
}
