package com.bombombom.devs.study;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.bombombom.devs.core.exception.BusinessRuleException;
import com.bombombom.devs.core.exception.ErrorCode;
import com.bombombom.devs.core.exception.ForbiddenException;
import com.bombombom.devs.core.exception.InvalidInputException;
import com.bombombom.devs.study.enums.StudyStatus;
import com.bombombom.devs.study.enums.VotingProcess;
import com.bombombom.devs.study.model.AlgorithmStudy;
import com.bombombom.devs.study.model.BookStudy;
import com.bombombom.devs.study.model.Round;
import com.bombombom.devs.study.model.Study;
import com.bombombom.devs.study.model.UserStudy;
import com.bombombom.devs.user.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
        study.admit(testuser);
        UserStudy userStudy = study.getUserStudies().getFirst();

        /*
         * Then
         */
        assertThat(userStudy.getUser()).isEqualTo(testuser);
        assertThat(userStudy.getStudy()).isEqualTo(study);
        assertThat(userStudy.getSecurityDeposit()).isEqualTo(study.getPenalty() * study.getWeeks());
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
        assertThatThrownBy(() -> study.admit(testuser))
            .isInstanceOf(BusinessRuleException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.STUDY_ENDED);
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
            .state(StudyStatus.READY)
            .build();

        /*
         * When & Then
         */

        assertThatThrownBy(() -> study.admit(testuser))
            .isInstanceOf(BusinessRuleException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.STUDY_IS_FULL);


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
        assertThatThrownBy(() -> study.admit(testuser))
            .isInstanceOf(BusinessRuleException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_ENOUGH_RELIABILITY);
    }

    @DisplayName("투표 관련 도메인 로직")
    @Nested
    class VoteDomainKnowledge {

        @DisplayName("중복 여부 설정")
        @Nested
        class SetDuplicatedTest {

            @DisplayName("기술서적 스터디가 아닌경우 중복여부 설정은 실패한다")
            @Test
            void set_duplicated_fail_if_not_book_study() {
                /*
                 * Given
                 */
                Study study = AlgorithmStudy.builder().build();

                /*
                 * When & Then
                 */

                assertThatThrownBy(
                    () -> study.setDuplicated(false))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.WRONG_STUDY_TYPE);
            }

            @DisplayName("투표 진행과정이 준비가 아닌경우 중복여부 설정은 실패한다")
            @Test
            void set_duplicated_fail_if_voting_process_not_ready() {
                /*
                 * Given
                 */
                Study study = BookStudy.builder()
                    .votingProcess(VotingProcess.ONGOING)
                    .build();

                /*
                 * When & Then
                 */

                assertThatThrownBy(
                    () -> study.setDuplicated(false))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.VOTING_PROCESS_NOT_READY);
            }
        }

        @Nested
        @DisplayName("투표시작 테스트")
        class StartVotingTest {

            @DisplayName("기술서적 스터디가 아닌경우 투표 시작은 실패한다")
            @Test
            void start_voting_fail_if_not_book_study() {
                /*
                 * Given
                 */
                Long userId = 1L;
                Study study = AlgorithmStudy.builder().build();

                /*
                 * When & Then
                 */

                assertThatThrownBy(
                    () -> study.startVoting(userId))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.WRONG_STUDY_TYPE);
            }

            @DisplayName("리더가 아닌경우 투표 시작은 실패한다")
            @Test
            void start_voting_fail_if_not_leader() {
                /*
                 * Given
                 */
                Long userId = 1L;
                User mockUser = mock(User.class);

                Study study = BookStudy.builder()
                    .leader(mockUser)
                    .build();

                when(mockUser.getId()).thenReturn(2L);

                /*
                 * When & Then
                 */

                assertThatThrownBy(
                    () -> study.startVoting(userId))
                    .isInstanceOf(ForbiddenException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ONLY_LEADER_ALLOWED);
            }

            @DisplayName("투표 진행 과정이 준비가 아니라면 투표 시작은 실패한다")
            @Test
            void start_voting_fail_if_voting_process_not_ready() {
                /*
                 * Given
                 */
                Long userId = 1L;
                User mockUser = mock(User.class);

                Study study = BookStudy.builder()
                    .leader(mockUser)
                    .votingProcess(VotingProcess.ONGOING)
                    .build();

                when(mockUser.getId()).thenReturn(userId);

                /*
                 * When & Then
                 */

                assertThatThrownBy(
                    () -> study.startVoting(userId))
                    .isInstanceOf(BusinessRuleException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.VOTING_PROCESS_NOT_READY);
            }
        }

        @Nested
        @DisplayName("투표 가능여부 검증")
        class CanVoteTest {

            @DisplayName("기술서적 스터디가 아닌경우 투표 가능여부 검증은 실패한다")
            @Test
            void can_vote_throw_if_not_book_study() {
                /*
                 * Given
                 */
                Long userId = 1L;
                Study study = AlgorithmStudy.builder().build();

                /*
                 * When & Then
                 */

                assertThatThrownBy(
                    study::canVote)
                    .isInstanceOf(BusinessRuleException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.WRONG_STUDY_TYPE);


            }

            @DisplayName("투표가 진행중이 아니라면 투표 가능여부 검증은 실패한다")
            @Test
            void can_vote_throw_if_voting_process_not_ongoing() {
                /*
                 * Given
                 */
                Study study = BookStudy.builder().build();

                /*
                 * When & Then
                 */

                assertThatThrownBy(
                    study::canVote)
                    .isInstanceOf(BusinessRuleException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.VOTING_PROCESS_NOT_ONGOING);


            }
        }


    }

    @Nested
    @DisplayName("과제목록 수정 가능여부 검증")
    class CanEditAssignmentTest {

        @DisplayName("기술서적 스터디가 아닌경우 과제 수정 가능여부 검증은 실패한다")
        @Test
        void can_edit_assignment_throw_if_not_book_study() {
            /*
             * Given
             */
            Long userId = 1L;
            Integer roundIdx = 2;
            Round nextRound = null;
            Study study = AlgorithmStudy.builder().build();

            /*
             * When & Then
             */

            assertThatThrownBy(
                () -> study.canEditAssignment(userId, roundIdx, nextRound))
                .isInstanceOf(BusinessRuleException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.WRONG_STUDY_TYPE);

        }

        @DisplayName("회차인덱스가 다음회차가 아닌경우 과제 수정 가능여부 검증은 실패한다")
        @Test
        void can_edit_assignment_throw_if_round_not_match() {
            /*
             * Given
             */
            Long userId = 1L;
            Integer roundIdx = 2;
            Round nextRound = Round.builder().idx(roundIdx + 1).build();
            Study study = BookStudy.builder().build();

            /*
             * When & Then
             */

            assertThatThrownBy(
                () -> study.canEditAssignment(userId, roundIdx, nextRound))
                .isInstanceOf(InvalidInputException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_NEXT_ROUND_IDX);

        }

        @DisplayName("회차인덱스가 다음회차가 아닌경우 과제 수정 가능여부 검증은 실패한다")
        @Test
        void can_edit_assignment_throw_if_voting_process_not_ready() {
            /*
             * Given
             */
            Long userId = 1L;
            Integer roundIdx = 2;
            Round nextRound = Round.builder().idx(roundIdx).build();
            Study study = BookStudy.builder()
                .votingProcess(VotingProcess.ONGOING).build();

            /*
             * When & Then
             */

            assertThatThrownBy(
                () -> study.canEditAssignment(userId, roundIdx, nextRound))
                .isInstanceOf(BusinessRuleException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.VOTING_PROCESS_NOT_READY);

        }

        @DisplayName("리더가 아닌경우 과제 수정 가능여부 검증은 실패한다")
        @Test
        void can_edit_assignment_throw_if_not_leader() {
            /*
             * Given
             */
            Long userId = 1L;
            Integer roundIdx = 2;
            Round nextRound = Round.builder().idx(roundIdx).build();
            Study study = BookStudy.builder()
                .votingProcess(VotingProcess.READY)
                .leader(User.builder().id(userId + 1).build()).build();

            /*
             * When & Then
             */

            assertThatThrownBy(
                () -> study.canEditAssignment(userId, roundIdx, nextRound))
                .isInstanceOf(ForbiddenException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ONLY_LEADER_ALLOWED);

        }

    }

}
