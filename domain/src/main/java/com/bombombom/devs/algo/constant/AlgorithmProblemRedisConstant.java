package com.bombombom.devs.algo.constant;

public class AlgorithmProblemRedisConstant {

    private static final String ALGORITHM_STUDY_PREFIX = "algo_study:";
    private static final String ALGORITHM_STUDY_ASSIGNMENT_PREFIX = "algo_study:assignment:";
    private static final String TASK_STATUS_UPDATE_SUFFIX = ":task_status:update";

    public static final String ALGORITHM_STUDY_ASSIGNMENT_QUEUE_KEY =
        ALGORITHM_STUDY_ASSIGNMENT_PREFIX + "queue";
    public static final String ALGORITHM_STUDY_ASSIGNMENT_QUEUE_CONSUMER_GROUP =
        ALGORITHM_STUDY_ASSIGNMENT_QUEUE_KEY + ":" + "group";
    public static final String ALGORITHM_STUDY_ASSIGNMENT_QUEUE_CONSUMER =
        ALGORITHM_STUDY_ASSIGNMENT_QUEUE_KEY + ":" + "consumer";

    public static String getAlgorithmAssignmentSolvedStatusUpdateKey(Long studyId) {
        return ALGORITHM_STUDY_PREFIX + studyId + TASK_STATUS_UPDATE_SUFFIX;
    }

}
