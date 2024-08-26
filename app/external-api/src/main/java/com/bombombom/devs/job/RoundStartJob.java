package com.bombombom.devs.job;

import static org.quartz.JobBuilder.newJob;

import com.bombombom.devs.algo.model.AlgorithmProblem;
import com.bombombom.devs.core.enums.AlgoTag;
import com.bombombom.devs.external.algo.service.AlgorithmProblemService;
import com.bombombom.devs.external.study.service.StudyService;
import com.bombombom.devs.solvedac.SolvedacClient;
import com.bombombom.devs.solvedac.dto.ProblemListResponse;
import com.bombombom.devs.study.model.AlgorithmStudy;
import com.bombombom.devs.study.model.Round;
import java.util.List;
import java.util.Map;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronScheduleBuilder;
import org.quartz.InterruptableJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.UnableToInterruptJobException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoundStartJob extends QuartzJobBean implements InterruptableJob {

    private static final String DEVELOPER_NAME = "송승훈";
    private static final String JOB_DESCRIPTION = "라운드 시작 작업";
    private static final String SCHEDULE_EXPRESSION = "0 0 0 * * ?";
    private static final String JOB_IDENTITY = "Dev";
    private static final String JOB_WORK = "Work";

    private final StudyService studyService;
    private final AlgorithmProblemService algorithmProblemService;
    private final SolvedacClient solvedacClient;
    private final AlgorithmProblemConverter algorithmProblemConverter;
    private boolean isInterrupted = false;

    public static Trigger buildJobTrigger() {
        return TriggerBuilder.newTrigger()
            .withSchedule(CronScheduleBuilder.cronSchedule(SCHEDULE_EXPRESSION)).build();
    }

    public static JobDetail buildJobDetail() {
        return newJob(RoundStartJob.class)
            .withIdentity("roundStartJob", "roundStartGroup")
            .usingJobData(newJobDataMap())
            .build();
    }

    /*
    RoundJob 은 해당 날짜에 시작하는 라운드들을 조회하여 문제를 선정 및 배정한다.
      1. 시작해야 하는 라운드들을 조회한다. (Study 와 Fetch Join)
      2. round 에 연결된 Study 의 종류에 따라 해야할 일을 수행한다.
    */
    private static JobDataMap newJobDataMap() {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(JOB_IDENTITY, RoundStartJob.DEVELOPER_NAME);
        jobDataMap.put(JOB_WORK, RoundStartJob.JOB_DESCRIPTION);
        return jobDataMap;
    }

    @Override
    public void interrupt() throws UnableToInterruptJobException {
        isInterrupted = true;
        log.error("RoundStartJob::interrupt() Called!");
    }

    @Override
    protected void executeInternal(@NonNull JobExecutionContext context) {
        long startTime = System.currentTimeMillis();

        List<Round> rounds = studyService.findRoundsHaveToStart();
        rounds.forEach(round -> {
            if (isInterrupted) {
                log.error("RoundStartJob is interrupted!");
                isInterrupted = false;
                return;
            }
            if (round.getStudy() instanceof AlgorithmStudy study) {
                startRoundOfAlgoStudy(study, round);
            }
        });

        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        log.debug("RoundJob 수행 시간: " + executionTime + "ms");
    }


    private void startRoundOfAlgoStudy(AlgorithmStudy study, Round round) {
        Map<AlgoTag, Integer> problemCountForEachTag =
            algorithmProblemService.getProblemCountForEachTag(study.getProblemCount());

        ProblemListResponse problemListResponse = solvedacClient.getUnSolvedProblems(
            study.getBaekjoonIds(), problemCountForEachTag, study.getDifficultySpreadForEachTag());

        List<AlgorithmProblem> problems = algorithmProblemConverter.convert(problemListResponse);

        List<AlgorithmProblem> unsolvedProblems =
            algorithmProblemService.findProblemsThenSaveWhenNotExist(problems);
        studyService.assignProblemToRound(round, unsolvedProblems);
    }

}