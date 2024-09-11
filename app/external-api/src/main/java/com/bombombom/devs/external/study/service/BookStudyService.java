package com.bombombom.devs.external.study.service;

import com.bombombom.devs.book.model.Book;
import com.bombombom.devs.book.repository.BookRepository;
import com.bombombom.devs.core.exception.BusinessRuleException;
import com.bombombom.devs.core.exception.DuplicationException;
import com.bombombom.devs.core.exception.ErrorCode;
import com.bombombom.devs.core.exception.ForbiddenException;
import com.bombombom.devs.core.exception.NotFoundException;
import com.bombombom.devs.core.util.Clock;
import com.bombombom.devs.external.study.controller.dto.request.EditAssignmentRequest.AssignmentInfo;
import com.bombombom.devs.external.study.service.dto.command.AddAssignmentCommand;
import com.bombombom.devs.external.study.service.dto.command.DeleteAssignmentCommand;
import com.bombombom.devs.external.study.service.dto.command.EditAssignmentCommand;
import com.bombombom.devs.external.study.service.dto.command.RegisterBookStudyCommand;
import com.bombombom.devs.external.study.service.dto.command.VoteAssignmentCommand;
import com.bombombom.devs.external.study.service.dto.result.AssignmentResult;
import com.bombombom.devs.external.study.service.dto.result.AssignmentVoteResult;
import com.bombombom.devs.external.study.service.dto.result.BookStudyResult;
import com.bombombom.devs.external.study.service.dto.result.progress.BookStudyProgress;
import com.bombombom.devs.study.enums.StudyType;
import com.bombombom.devs.study.enums.VotingProcess;
import com.bombombom.devs.study.model.Assignment;
import com.bombombom.devs.study.model.AssignmentVote;
import com.bombombom.devs.study.model.BookStudy;
import com.bombombom.devs.study.model.Problem;
import com.bombombom.devs.study.model.Round;
import com.bombombom.devs.study.model.Study;
import com.bombombom.devs.study.model.UserAssignment;
import com.bombombom.devs.study.model.Video;
import com.bombombom.devs.study.repository.AssignmentRepository;
import com.bombombom.devs.study.repository.AssignmentVoteRepository;
import com.bombombom.devs.study.repository.ProblemRepository;
import com.bombombom.devs.study.repository.RoundRepository;
import com.bombombom.devs.study.repository.StudyRepository;
import com.bombombom.devs.study.repository.UserAssignmentRepository;
import com.bombombom.devs.study.repository.UserStudyRepository;
import com.bombombom.devs.study.repository.VideoRepository;
import com.bombombom.devs.user.model.User;
import com.bombombom.devs.user.repository.UserRepository;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookStudyService implements StudyProgressService {

    private final Clock clock;
    private final StudyRepository studyRepository;
    private final BookRepository bookRepository;
    private final RoundRepository roundRepository;
    private final UserRepository userRepository;
    private final UserStudyRepository userStudyRepository;
    private final AssignmentRepository assignmentRepository;

    private final UserAssignmentRepository userAssignmentRepository;
    private final VideoRepository videoRepository;
    private final ProblemRepository problemRepository;
    private final AssignmentVoteRepository assignmentVoteRepository;

    @Override
    public StudyType getStudyType() {
        return StudyType.BOOK;
    }

    @Override
    public BookStudyProgress findStudyProgress(Round round, List<User> members) {
        List<Long> memberIds = members.stream().map(User::getId).toList();
        List<Assignment> assignments = assignmentRepository.findAllByRound(round);

        List<UserAssignment> userAssignments = userAssignmentRepository
            .findAllByAssignmentInAndUserIdIn(assignments, memberIds);

        List<Video> videos = videoRepository
            .findAllByAssignmentInAndUploaderIdIn(assignments, memberIds);

        List<Problem> problems = problemRepository
            .findAllByAssignmentInAndExaminerIdIn(assignments, memberIds);

        return BookStudyProgress.fromEntity(round, assignments, userAssignments, problems, videos);
    }

    @Override
    public void startRound(Study study, Round round) {
    }

    @Transactional
    public BookStudyResult createStudy(
        Long userId, RegisterBookStudyCommand registerBookStudyCommand) {

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

        Book book = bookRepository.findByIsbn(registerBookStudyCommand.isbn())
            .orElseThrow(() -> new NotFoundException(ErrorCode.BOOK_NOT_FOUND));

        BookStudy bookStudy = BookStudy.builder()
            .name(registerBookStudyCommand.name())
            .introduce(registerBookStudyCommand.introduce())
            .capacity(registerBookStudyCommand.capacity())
            .weeks(registerBookStudyCommand.weeks())
            .startDate(registerBookStudyCommand.startDate())
            .reliabilityLimit(registerBookStudyCommand.reliabilityLimit())
            .penalty(registerBookStudyCommand.penalty())
            .headCount(registerBookStudyCommand.headCount())
            .state(registerBookStudyCommand.state())
            .leader(user)
            .book(book)
            .votingProcess(VotingProcess.READY)
            .build();

        bookStudy.createRounds();

        bookStudy.admit(user);

        studyRepository.save(bookStudy);

        if (bookStudy.getStartDate().equals(clock.today())) {
            bookStudy.start(clock, userId);

            startRound(bookStudy, bookStudy.getFirstRound());
        }

        user.payMoney(bookStudy.calculateDeposit());
        return BookStudyResult.fromEntity(bookStudy);
    }

    @Transactional
    public List<AssignmentResult> addAssignments(Long userId, Long studyId,
        AddAssignmentCommand addAssignmentCommand) {
        Round nextRound = roundRepository.findTop1RoundByStudyIdAndStartDateAfterOrderByIdx(studyId,
                clock.today())
            .orElseThrow(() -> new NotFoundException(ErrorCode.NEXT_ROUND_NOT_FOUND));

        Study study = studyRepository.findWithLeaderById(
                studyId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.STUDY_NOT_FOUND));

        study.canEditAssignment(userId,
            addAssignmentCommand.roundIdx(),
            nextRound);

        List<Assignment> assignments =
            addAssignmentCommand.assignments()
                .stream().map(
                    assignmentInfo ->
                        Assignment.builder()
                            .round(nextRound)
                            .title(assignmentInfo.title())
                            .description(assignmentInfo.description())
                            .pageStart(assignmentInfo.pageStart())
                            .pageEnd(assignmentInfo.pageEnd())
                            .build()
                ).collect(Collectors.toList());

        return assignmentRepository.saveAll(assignments).stream().map(AssignmentResult::fromEntity)
            .toList();
    }

    @Transactional
    public void removeAssignments(Long userId, Long studyId,
        DeleteAssignmentCommand deleteAssignmentCommand) {
        Round nextRound = roundRepository.findTop1RoundByStudyIdAndStartDateAfterOrderByIdx(studyId,
                clock.today())
            .orElseThrow(() -> new NotFoundException(ErrorCode.NEXT_ROUND_NOT_FOUND));

        Study study = studyRepository.findWithLeaderById(
                studyId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.STUDY_NOT_FOUND));

        study.canEditAssignment(userId,
            deleteAssignmentCommand.roundIdx(),
            nextRound);
        if (assignmentRepository.existsAllByIdInAndRoundNot(
            deleteAssignmentCommand.assignmentIds(), nextRound)) {
            throw new BusinessRuleException(ErrorCode.NOT_NEXT_ROUND_ASSIGNMENT);
        }
        assignmentRepository.deleteAllByIdInAndRound(deleteAssignmentCommand.assignmentIds(),
            nextRound);

    }

    @Transactional
    public List<AssignmentResult> setAssignments(Long userId, Long studyId,
        EditAssignmentCommand editAssignmentCommand) {

        Round nextRound = roundRepository.findTop1RoundByStudyIdAndStartDateAfterOrderByIdx(studyId,
                clock.today())
            .orElseThrow(() -> new NotFoundException(ErrorCode.NEXT_ROUND_NOT_FOUND));

        Study study = studyRepository.findWithLeaderById(
                studyId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.STUDY_NOT_FOUND));

        study.canEditAssignment(userId,
            editAssignmentCommand.roundIdx(),
            nextRound);

        Set<Long> assignmentIds = editAssignmentCommand.assignments().stream()
            .map(AssignmentInfo::id).collect(Collectors.toSet());

        if (assignmentIds.size() != editAssignmentCommand.assignments().size()) {
            throw new DuplicationException(ErrorCode.DUPLICATED_ASSIGNMENT_ID);
        }

        List<Assignment> assignments = assignmentRepository.findAllById(assignmentIds);

        if (assignments.size() != assignmentIds.size()) {
            throw new NotFoundException(ErrorCode.ASSIGNMENT_NOT_FOUND);
        }

        if (assignments.stream().anyMatch(assignment -> !assignment.getRound().equals(nextRound))) {

            throw new BusinessRuleException(ErrorCode.NOT_NEXT_ROUND_ASSIGNMENT);
        }

        List<Assignment> updatedAssignments =
            editAssignmentCommand.assignments()
                .stream().map(
                    assignmentInfo ->
                        Assignment.builder().id(assignmentInfo.id())
                            .round(nextRound)
                            .title(assignmentInfo.title())
                            .description(assignmentInfo.description())
                            .pageStart(assignmentInfo.pageStart())
                            .pageEnd(assignmentInfo.pageEnd())
                            .build()
                ).collect(Collectors.toList());

        return assignmentRepository.saveAll(updatedAssignments).stream()
            .map(AssignmentResult::fromEntity)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<AssignmentResult> getAssignments(Long studyId,
        Integer roundIdx) {

        Round round = roundRepository.findRoundByStudyAndIdx(studyId,
                roundIdx)
            .orElseThrow(() -> new NotFoundException(ErrorCode.ROUND_NOT_FOUND));

        return assignmentRepository.findAllByRound(round).stream()
            .map(AssignmentResult::fromEntity)
            .toList();
    }

    public AssignmentVoteResult voteAssignment(Long userId, Long studyId,
        VoteAssignmentCommand voteAssignmentCommand) {

        Study study = studyRepository.findById(studyId)
            .orElseThrow(() -> new NotFoundException(ErrorCode.STUDY_NOT_FOUND));

        study.canVote();
        
        if (!userStudyRepository.existsByUserIdAndStudyId(userId, studyId)) {
            throw new ForbiddenException(ErrorCode.ONLY_MEMBER_ALLOWED);
        }

        Round nextRound = roundRepository.findTop1RoundByStudyIdAndStartDateAfterOrderByIdx(studyId,
                clock.today())
            .orElseThrow(() -> new NotFoundException(ErrorCode.NEXT_ROUND_NOT_FOUND));

        Assignment first = assignmentRepository.findById(voteAssignmentCommand.first())
            .orElseThrow(() -> new NotFoundException(ErrorCode.ASSIGNMENT_NOT_FOUND));

        if (!nextRound.equals(first.getRound())) {
            throw new BusinessRuleException(ErrorCode.NOT_NEXT_ROUND_ASSIGNMENT);
        }

        Assignment second;

        if (voteAssignmentCommand.second() != null) {
            second = assignmentRepository.findById(voteAssignmentCommand.second())
                .orElseThrow(() -> new NotFoundException(ErrorCode.ASSIGNMENT_NOT_FOUND));

            if (!nextRound.equals(second.getRound())) {
                throw new BusinessRuleException(ErrorCode.NOT_NEXT_ROUND_ASSIGNMENT);
            }
        } else {
            second = null;
        }

        AssignmentVote vote = assignmentVoteRepository.findByUserIdAndRound(userId,
                nextRound)
            .orElseGet(
                () -> {
                    User user = userRepository.findById(userId)
                        .orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

                    return AssignmentVote.builder()
                        .user(user)
                        .first(first)
                        .second(second)
                        .round(nextRound)
                        .build();
                });

        vote.update(first, second);

        return AssignmentVoteResult.fromEntity(assignmentVoteRepository.save(vote));

    }
}
