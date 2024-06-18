package com.bombombom.devs.study.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.bombombom.devs.study.models.AlgorithmStudy;
import com.bombombom.devs.study.models.Study;
import com.bombombom.devs.study.models.StudyStatus;
import com.bombombom.devs.study.models.UserStudy;
import com.bombombom.devs.user.models.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StudyTest {

    @Test
    @DisplayName("유저는 스터디에 가입할 수 있다.")
    void user_can_join_study() {
        /*
         * Given
         */
        User testuser = User.builder()
            .id(1L)
            .username("testuser")
            .money(10000)
            .reliability(10)
            .build();
        Study study = AlgorithmStudy.builder()
            .capacity(10)
            .headCount(1)
            .weeks(10)
            .reliabilityLimit(10)
            .penalty(1000)
            .state(StudyStatus.READY)
            .build();


        /*
         * When
         */
        UserStudy userStudy = study.join(testuser);

        /*
         * Then
         */
        assertThat(userStudy.getUser()).isEqualTo(testuser);
        assertThat(userStudy.getStudy()).isEqualTo(study);
        assertThat(userStudy.getSecurityDeposit()).isEqualTo(study.getPenalty() * study.getWeeks());
    }

    @Test
    @DisplayName("유저는 이미 가입한 스터디에 다시 가입할 수 없다.")
    void user_cannot_join_study_twice() {
        /*
         * Given
         */
        User testuser = User.builder()
            .id(1L)
            .username("testuser")
            .money(10000)
            .reliability(10)
            .build();
        Study study = AlgorithmStudy.builder()
            .capacity(10)
            .headCount(1)
            .weeks(10)
            .reliabilityLimit(10)
            .penalty(1000)
            .state(StudyStatus.READY)
            .build();
        study.join(testuser);

        /*
         * When & Then
         */
        assertThatThrownBy(() -> study.join(testuser))
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("유저는 이미 끝난 스터디에 다시 가입할 수 없다.")
    void user_cannot_join_end_state_study() {
        /*
         * Given
         */
        User testuser = User.builder()
            .id(1L)
            .username("testuser")
            .money(10000)
            .reliability(10)
            .build();
        Study study = AlgorithmStudy.builder()
            .capacity(10)
            .headCount(1)
            .weeks(10)
            .reliabilityLimit(10)
            .penalty(1000)
            .state(StudyStatus.END)
            .build();

        /*
         * When & Then
         */
        assertThatThrownBy(() -> study.join(testuser))
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("유저는 정원이 꽉찬 스터디에 다시 가입할 수 없다.")
    void user_cannot_join_full_study() {
        /*
         * Given
         */
        User testuser = User.builder()
            .id(1L)
            .username("testuser")
            .money(10000)
            .reliability(10)
            .build();
        Study study = AlgorithmStudy.builder()
            .capacity(10)
            .headCount(10)
            .weeks(10)
            .reliabilityLimit(10)
            .penalty(1000)
            .state(StudyStatus.END)
            .build();

        /*
         * When & Then
         */
        assertThatThrownBy(() -> study.join(testuser))
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("유저의 신뢰도가 스터디의 신뢰도 제한보다 낮으면 가입할 수 없다.")
    void user_cannot_join_study_with_low_reliability() {
        /*
         * Given
         */
        User testuser = User.builder()
            .id(1L)
            .username("testuser")
            .money(10000)
            .reliability(5)
            .build();
        Study study = AlgorithmStudy.builder()
            .capacity(10)
            .headCount(1)
            .weeks(10)
            .reliabilityLimit(10)
            .penalty(1000)
            .state(StudyStatus.READY)
            .build();

        /*
         * When & Then
         */
        assertThatThrownBy(() -> study.join(testuser))
            .isInstanceOf(IllegalStateException.class);
    }

}
