package com.bombombom.devs.study.model;

import com.bombombom.devs.common.BaseEntity;
import com.bombombom.devs.core.exception.BusinessRuleException;
import com.bombombom.devs.core.exception.ErrorCode;
import com.bombombom.devs.core.exception.ForbiddenException;
import com.bombombom.devs.core.util.Clock;
import com.bombombom.devs.study.enums.StudyStatus;
import com.bombombom.devs.study.enums.StudyType;
import com.bombombom.devs.user.model.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@Table(name = "study")
@DiscriminatorColumn(name = "STUDY_TYPE")
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Study extends BaseEntity {

    public static final int MAX_CAPACITY = 20;
    public static final int MAX_WEEKS = 52;

    public static final int MAX_RELIABILITY_LIMIT = 100;
    public static final int MAX_PENALTY = 100_000;
    public static final int MIN_DIFFICULTY_LEVEL = 1;
    public static final int MAX_DIFFICULTY_LEVEL = 30;
    public static final int MAX_PROBLEM_COUNT = 20;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(nullable = false)
    protected String name;

    @Column(nullable = false, length = 500)
    protected String introduce;

    @Column
    protected Integer capacity;

    @Column(nullable = false, name = "head_count")
    protected Integer headCount;

    @Column
    protected Integer weeks;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_id",
        nullable = false,
        foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private User leader;

    @Column(name = "start_date")
    protected LocalDate startDate;

    @Column(name = "reliability_limit")
    protected Integer reliabilityLimit;

    @Column
    protected Integer penalty;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    protected StudyStatus state;

    @OneToMany(mappedBy = "study", cascade = CascadeType.PERSIST)
    @Builder.Default
    protected List<UserStudy> userStudies = new ArrayList<>();

    @OneToMany(mappedBy = "study", cascade = CascadeType.PERSIST)
    @Builder.Default
    protected List<Round> rounds = new ArrayList<>();

    public abstract StudyType getStudyType();


    public Integer calculateDeposit() {
        return penalty * weeks;
    }

    public void admit(User user) {
        if (state.equals(StudyStatus.END)) {

            throw new BusinessRuleException(ErrorCode.STUDY_ENDED);
        }
        if (headCount >= capacity) {
            throw new BusinessRuleException(ErrorCode.STUDY_IS_FULL);

        }
        if (reliabilityLimit != null && user.getReliability() < reliabilityLimit) {
            throw new BusinessRuleException(ErrorCode.NOT_ENOUGH_RELIABILITY);
        }
        UserStudy userStudy = UserStudy.of(user, this, calculateDeposit());

        headCount++;
        userStudies.add(userStudy);
    }

    public List<String> getBaekjoonIds() {
        return userStudies.stream()
            .map(userStudy -> userStudy.getUser().getBaekjoon())
            .toList();
    }

    public void createRounds() {
        for (int i = 0; i < weeks; i++) {
            createRound(i);
        }
    }

    private void createRound(int idx) {
        Round round = Round.builder()
            .study(this)
            .idx(idx)
            .startDate(startDate.plusWeeks(idx))
            .endDate(startDate.plusWeeks(idx + 1))
            .build();
        rounds.add(round);
    }

    private void changeStartDate(LocalDate date) {
        if (startDate == date) {
            return;
        }
        startDate = date;
        IntStream.range(0, rounds.size())
            .forEach(
                idx -> {
                    Round round = rounds.get(idx);
                    round.changeDate(date);
                }
            );


    }

    public Round getFirstRound() {
        return rounds.getFirst();
    }

    public void start(Clock clock, Long userId) {
        changeStartDate(clock.today());

        if (!leader.getId().equals(userId)) {
            throw new ForbiddenException(ErrorCode.ONLY_LEADER_ALLOWED);
        }

        if (state != StudyStatus.READY) {
            throw new BusinessRuleException(ErrorCode.STUDY_STARTED);
        }

        state = StudyStatus.RUNNING;
    }

    public void assertLeader(Long userId) {
        if (!leader.getId().equals(userId)) {
            throw new ForbiddenException(ErrorCode.ONLY_LEADER_ALLOWED);
        }
    }


    public void setDuplicated(Boolean duplicated) {
        throw new BusinessRuleException(ErrorCode.WRONG_STUDY_TYPE);
    }


    public void startVoting(Long userId) {
        throw new BusinessRuleException(ErrorCode.WRONG_STUDY_TYPE);
    }

    public void canVote() {
        throw new BusinessRuleException(ErrorCode.WRONG_STUDY_TYPE);
    }

    public void canEditAssignment(Long userId, Integer roundIdx,
        Round nextRound) {
        throw new BusinessRuleException(ErrorCode.WRONG_STUDY_TYPE);
    }
}
