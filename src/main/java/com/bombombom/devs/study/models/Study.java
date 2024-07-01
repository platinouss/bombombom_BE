package com.bombombom.devs.study.models;

import com.bombombom.devs.global.audit.BaseEntity;
import com.bombombom.devs.user.models.User;
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
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AccessLevel;
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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @NotNull
    @Column
    protected String name;

    @NotNull
    @Column(length = 500)
    protected String introduce;

    @Column
    protected Integer capacity;

    @NotNull
    @Column(name = "head_count")
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

    @NotNull
    @Enumerated(EnumType.STRING)
    protected StudyStatus state;

    abstract public StudyType getStudyType();

    public UserStudy join(User user) {
        if (state.equals(StudyStatus.END)) {
            throw new IllegalStateException("The Study is End");
        }
        if (headCount >= capacity) {
            throw new IllegalStateException("The Study is full");
        }
        if (reliabilityLimit != null && user.getReliability() < reliabilityLimit) {
            throw new IllegalStateException("User reliability is low");
        }
        user.payMoney(penalty * weeks);
        headCount++;

        return UserStudy.of(user, this, penalty * weeks);
    }
}
