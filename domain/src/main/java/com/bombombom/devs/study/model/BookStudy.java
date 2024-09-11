package com.bombombom.devs.study.model;


import com.bombombom.devs.book.model.Book;
import com.bombombom.devs.core.exception.BusinessRuleException;
import com.bombombom.devs.core.exception.ErrorCode;
import com.bombombom.devs.core.exception.InvalidInputException;
import com.bombombom.devs.study.enums.StudyType;
import com.bombombom.devs.study.enums.VotingProcess;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Entity
@Getter
@SuperBuilder
@Table(name = "book_study")
@DiscriminatorValue(StudyType.Values.BOOK)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookStudy extends Study {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "book_id",
        nullable = false,
        foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Book book;

    @Column(nullable = false, name = "voting_process")
    @Enumerated(EnumType.STRING)
    protected VotingProcess votingProcess;

    @Override
    public StudyType getStudyType() {
        return StudyType.BOOK;
    }

    @Override
    public void startVoting(Long userId) {
        assertLeader(userId);

        if (votingProcess != VotingProcess.READY) {
            throw new BusinessRuleException(ErrorCode.VOTING_PROCESS_NOT_READY);
        }

        votingProcess = VotingProcess.ONGOING;
    }

    @Override
    public void canVote() {
        if (votingProcess != VotingProcess.ONGOING) {
            throw new BusinessRuleException(ErrorCode.VOTING_PROCESS_NOT_ONGOING);
        }
    }

    @Override
    public void canEditAssignment(Long userId, Integer roundIdx, Round nextRound) {

        if (!roundIdx.equals(nextRound.getIdx())) {
            throw new InvalidInputException(ErrorCode.NOT_NEXT_ROUND_IDX);
        }

        if (votingProcess != VotingProcess.READY) {
            throw new BusinessRuleException(ErrorCode.VOTING_PROCESS_NOT_READY);
        }

        assertLeader(userId);


    }
}
