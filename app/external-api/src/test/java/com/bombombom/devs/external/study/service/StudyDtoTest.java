package com.bombombom.devs.external.study.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import com.bombombom.devs.core.exception.BusinessRuleException;
import com.bombombom.devs.core.exception.ErrorCode;
import com.bombombom.devs.core.exception.NotFoundException;
import com.bombombom.devs.external.study.service.dto.result.progress.BookStudyProgress;
import com.bombombom.devs.study.model.Assignment;
import com.bombombom.devs.study.model.Problem;
import com.bombombom.devs.study.model.Round;
import com.bombombom.devs.study.model.UserAssignment;
import com.bombombom.devs.study.model.Video;
import com.bombombom.devs.user.model.User;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class StudyDtoTest {


    @Nested
    @DisplayName("Result DTO")
    class ResultDtoTest {

        @DisplayName("비디오의 업로더와 과제 관계가 없는 경우 기술서적 스터디 상세정보 결과 변환이 실패한다")
        @Test
        void book_study_result_from_entity_fail_if_not_found_user_assignment_for_video() {
            /*
             * Given
             */

            User user1 = User.builder().id(1L).build();

            List<Assignment> assignments = List.of(
                Assignment.builder().id(2L).build()
            );
            List<Video> videos = List.of(
                Video.builder()
                    .uploader(user1)
                    .assignment(assignments.getFirst()).build()
            );
            ;
            List<Problem> problems = List.of(
                Problem.builder().build()
            );
            ;
            List<UserAssignment> userAssignments = List.of(
            );
            ;

            /*
             * When & Then
             */
            assertThatThrownBy(() -> BookStudyProgress.fromEntity(
                mock(Round.class), assignments, userAssignments, problems, videos
            ))
                .isInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_ASSIGNMENT_NOT_FOUND);
        }

        @DisplayName("비디오의 업로더와 과제 관계가 다른 경우 기술서적 스터디 상세정보 결과 변환이 실패한다")
        @Test
        void book_study_result_from_entity_fail_if_differ_user_assignment_for_video() {
            /*
             * Given
             */

            User user1 = User.builder().id(1L).build();

            List<Assignment> assignments = List.of(
                Assignment.builder().id(2L).build(),

                Assignment.builder().id(3L).build()
            );
            List<Video> videos = List.of(
                Video.builder()
                    .uploader(user1)
                    .assignment(assignments.getLast()).build()
            );
            ;
            List<Problem> problems = List.of(
                Problem.builder().build()
            );
            ;
            List<UserAssignment> userAssignments = List.of(
                UserAssignment.builder().user(user1).assignment(assignments.getFirst()).build()
            );
            ;

            /*
             * When & Then
             */
            assertThatThrownBy(() -> BookStudyProgress.fromEntity(
                mock(Round.class), assignments, userAssignments, problems, videos
            ))
                .isInstanceOf(BusinessRuleException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.VIDEO_ASSIGNMENT_ID_NOT_MATCH);
        }

        @DisplayName("문제의 제작자와 과제 관계를 찾을 수 없는경우 기술서적 스터디 상세정보 결과 변환이 실패한다")
        @Test
        void book_study_result_from_entity_fail_if_not_found_user_assignment_for_problem() {
            /*
             * Given
             */

            User user1 = User.builder().id(1L).build();
            User user2 = User.builder().id(4L).build();

            List<Assignment> assignments = List.of(
                Assignment.builder().id(2L).build(),

                Assignment.builder().id(3L).build()
            );
            List<Video> videos = List.of(
                Video.builder()
                    .id(5L)
                    .uploader(user1)
                    .assignment(assignments.getFirst()).build()
            );
            ;
            List<Problem> problems = List.of(
                Problem.builder()
                    .assignment(assignments.getLast())
                    .examiner(user2).build()
            );
            ;
            List<UserAssignment> userAssignments = List.of(
                UserAssignment.builder().user(user1).assignment(assignments.getFirst()).build()
            );
            ;

            /*
             * When & Then
             */
            assertThatThrownBy(() -> BookStudyProgress.fromEntity(
                mock(Round.class), assignments, userAssignments, problems, videos
            ))
                .isInstanceOf(NotFoundException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_ASSIGNMENT_NOT_FOUND);


        }

        @DisplayName("문제의 제작자와 과제 관계가 다른 경우 기술서적 스터디 상세정보 결과 변환이 실패한다")
        @Test
        void book_study_result_from_entity_fail_if_not_match_user_assignment_for_problem() {
            /*
             * Given
             */

            User user1 = User.builder().id(1L).build();
            User user2 = User.builder().id(4L).build();

            List<Assignment> assignments = List.of(
                Assignment.builder().id(2L).build(),

                Assignment.builder().id(3L).build()
            );
            List<Video> videos = List.of(
                Video.builder()
                    .id(5L)
                    .uploader(user1)
                    .assignment(assignments.getFirst()).build()
            );
            ;
            List<Problem> problems = List.of(
                Problem.builder()
                    .assignment(assignments.getFirst())
                    .examiner(user2).build()
            );
            ;
            List<UserAssignment> userAssignments = List.of(
                UserAssignment.builder().user(user1).assignment(assignments.getFirst()).build(),
                UserAssignment.builder().user(user2).assignment(assignments.getLast()).build()
            );
            ;

            /*
             * When & Then
             */
            assertThatThrownBy(() -> BookStudyProgress.fromEntity(
                mock(Round.class), assignments, userAssignments, problems, videos
            ))
                .isInstanceOf(BusinessRuleException.class)
                .hasFieldOrPropertyWithValue("errorCode",
                    ErrorCode.PROBLEM_ASSIGNMENT_ID_NOT_MATCH);


        }
    }

}
