package com.bombombom.devs.coupon.repository;

import com.bombombom.devs.coupon.model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

}
