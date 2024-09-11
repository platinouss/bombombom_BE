package com.bombombom.devs.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.bombombom.devs.core.exception.BusinessRuleException;
import com.bombombom.devs.core.exception.ErrorCode;
import com.bombombom.devs.user.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserTest {

    @Test
    @DisplayName("유저는 자신이 가진 재화 이내에서 재화를 지불할 수 있다.")
    void user_can_pay_money_within_his_money() {
        /*
         * Given
         */
        User testuser = User.builder()
            .id(1L)
            .username("testuser")
            .money(1000)
            .build();

        /*
         * When
         */
        testuser.payMoney(500);

        /*
         * Then
         */
        assertThat(testuser.getMoney()).isEqualTo(500);
    }

    @Test
    @DisplayName("유저가 가진 재화보다 많은 재화를 지불할 수 없다.")
    void user_can_not_pay_more_than_have() {
        /*
         * Given
         */
        User testuser = User.builder()
            .id(1L)
            .username("testuser")
            .money(1000)
            .build();

        /*
         * When & Then
         */
        assertThatThrownBy(() -> testuser.payMoney(1500))
            .isInstanceOf(BusinessRuleException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_ENOUGH_MONEY);
    }

}
