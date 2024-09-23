package com.bombombom.devs.external.study.service;

import com.bombombom.devs.book.model.Book;
import com.bombombom.devs.book.repository.BookRepository;
import com.bombombom.devs.core.exception.BusinessRuleException;
import com.bombombom.devs.core.exception.DuplicationException;
import com.bombombom.devs.core.exception.ErrorCode;
import com.bombombom.devs.core.exception.ForbiddenException;
import com.bombombom.devs.core.exception.NotFoundException;
import com.bombombom.devs.core.util.Clock;
import com.bombombom.devs.core.util.Util;
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
import com.bombombom.devs.study.model.UserStudy;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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

    private List<UserAssignment> countingVoteWithDuplication(
        List<AssignmentVote> votes, List<Assignment> assignments,
        List<User> members
    ) {

        List<UserAssignment> userAssignments = new ArrayList<>();
        Set<User> availableMember = new HashSet<>(members);
        Set<Assignment> unassignedAssignment = new HashSet<>(assignments);

        final Set<Long> assignmentIdSet = assignments.stream().map(Assignment::getId)
            .collect(Collectors.toSet());
        final Set<Long> memberIdSet = members.stream().map(User::getId)
            .collect(Collectors.toSet());

        votes.forEach(
            vote -> {
                Assignment first = vote.getFirst();
                User member = vote.getUser();
                if (!assignmentIdSet.contains(first.getId())) {
                    throw new BusinessRuleException(ErrorCode.INVALID_VOTE_FOR_UNKNOWN_ASSIGNMENT);
                }
                if (!memberIdSet.contains(member.getId())) {
                    throw new BusinessRuleException(ErrorCode.INVALID_VOTE_BY_NOT_MEMBER);
                }

                if (!availableMember.contains(member)) {
                    throw new BusinessRuleException(ErrorCode.MULTIPLE_VOTE);
                }

                userAssignments.add(UserAssignment.builder()
                    .assignment(first)
                    .user(member)
                    .build()
                );

                availableMember.remove(member);
                unassignedAssignment.remove(first);
            }
        );

        // 투표를 안한사람들
        availableMember.forEach(
            member -> {
                Assignment randomAssignment;
                if (unassignedAssignment.isEmpty()) {
                    randomAssignment = Util.getRandom(assignments);
                } else {
                    randomAssignment = Util.getRandom(unassignedAssignment);
                    unassignedAssignment.remove(randomAssignment);
                }
                userAssignments.add(UserAssignment.builder()
                    .assignment(randomAssignment)
                    .user(member)
                    .build()
                );
            }
        );
        return userAssignments;
    }


    private List<UserAssignment> countingVote(
        List<AssignmentVote> votes, List<Assignment> assignments,
        List<User> members) {

        // votersMap
        // key: 과제 ID
        // value: [ [해당 과제를 1순위로 투표한 유저 ID], [해당 과제를 2순위로 투표한 유저 ID] ]
        Map<Long, List<List<User>>> votersMap =
            assignments.stream().collect(Collectors.toMap(
                Assignment::getId, a -> List.of(new ArrayList<>(), new ArrayList<>())
            ));

        List<UserAssignment> userAssignments = new ArrayList<>();
        Set<User> availableMember = new HashSet<>(members);
        Set<Assignment> unassignedAssignment = new HashSet<>(assignments);
        Map<Long, Assignment> userIdFirstChoiceMap = new HashMap<>();

        // 투표 하나씩 까보고 votersMap 수정
        votes.forEach(
            vote -> {
                Assignment first = vote.getFirst();
                Assignment second = vote.getSecond();
                User member = vote.getUser();

                if (!availableMember.contains(member)) {
                    throw new BusinessRuleException(ErrorCode.INVALID_VOTE_BY_NOT_MEMBER);
                }

                if (!votersMap.containsKey(first.getId())) {
                    throw new BusinessRuleException(ErrorCode.INVALID_VOTE_FOR_UNKNOWN_ASSIGNMENT);
                }
                votersMap.get(first.getId()).getFirst().add(member);

                if (second != null) {
                    if (!votersMap.containsKey(second.getId())) {
                        throw new BusinessRuleException(
                            ErrorCode.INVALID_VOTE_FOR_UNKNOWN_ASSIGNMENT);
                    }
                    votersMap.get(second.getId()).getLast().add(member);
                }

                if (userIdFirstChoiceMap.containsKey(member.getId())) {
                    throw new BusinessRuleException(ErrorCode.MULTIPLE_VOTE);
                }
                userIdFirstChoiceMap.put(member.getId(), vote.getFirst());
            }
        );

        // 각 과제별로 1순위,2순위 투표에 대한 추첨 진행
        // 할당되지 않은 과제와 가능한 멤버 집합을 계속 관리
        IntStream.range(0, 2).forEach(
            priority -> {
                Iterator<Assignment> iterator = unassignedAssignment.iterator();
                while (iterator.hasNext()) {
                    Assignment assignment = iterator.next();
                    List<User> voters = votersMap.get(assignment.getId()).get(priority)
                        .stream().filter(availableMember::contains).toList();

                    if (voters.isEmpty()) {
                        continue;
                    }
                    User winner = Util.getRandom(voters);
                    iterator.remove();
                    availableMember.remove(winner);

                    userAssignments.add(UserAssignment.builder()
                        .assignment(assignment)
                        .user(winner)
                        .build()
                    );
                }
            }
        );

        // 기권한멤버 및 1순위, 2순위 투표에서 모두 낙첨된 멤버는 우선적으로 미할당과제에 배치하되
        // 모든 과제가 할당 되었다면 1순위로 투표한 과제를 중복할당받게됨
        // sort하는 이유는 기권한멤버가 투표한 멤버보다 먼저 남은 과제를 할당받도록 함
        availableMember.stream()
            .sorted(
                Comparator.comparing(member -> userIdFirstChoiceMap.containsKey(member.getId())))
            .forEach(
                member -> {
                    Assignment randomAssignment;
                    if (unassignedAssignment.isEmpty()) {
                        randomAssignment = userIdFirstChoiceMap.getOrDefault(member.getId(),
                            Util.getRandom(assignments));
                    } else {
                        randomAssignment = Util.getRandom(unassignedAssignment);
                        unassignedAssignment.remove(randomAssignment);
                    }
                    userAssignments.add(UserAssignment.builder()
                        .assignment(randomAssignment)
                        .user(member)
                        .build()
                    );
                }
            );

        return userAssignments;
    }

    @Override
    public void startRound(Study study, Round round) {

        List<Assignment> assignments = assignmentRepository.findAllByRound(round);
        if (assignments.isEmpty()) {
            return;
        }

        BookStudy bookStudy = (BookStudy) study;

        List<User> members = userStudyRepository.findWithUserByStudyId(study.getId()).stream()
            .map(UserStudy::getUser).toList();

        List<AssignmentVote> votes = assignmentVoteRepository.findAllByRound(round);

        List<UserAssignment> userAssignments;
        if (bookStudy.isDuplicated()) {
            userAssignments = countingVoteWithDuplication(votes, assignments, members);
        } else {
            userAssignments = countingVote(votes, assignments, members);
        }
        bookStudy.endVote();
        userAssignmentRepository.saveAll(userAssignments);

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

        // lock
        // mysql - repeatable read (phantom read) 격리레벨 강제로 상위로 올리기?
        // unit test에서 쓰레드 여러개 쏴보기
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
