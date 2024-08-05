package com.bombombom.devs.common.constant;

public class AlgorithmProblemRedisConstant {

    private static final String ALGORITHM_STUDY_PREFIX = "algo_study:";
    private static final String TASK_STATUS_PREFIX = "task:status:";
    private static final String TASK_STATUS_UPDATE_SUFFIX = TASK_STATUS_PREFIX + "update";

    public static final String TASK_STATUS_UPDATE_QUEUE_KEY = TASK_STATUS_PREFIX + "queue";
    public static final String TASK_STATUS_UPDATE_QUEUE_CONSUMER_GROUP =
        TASK_STATUS_UPDATE_QUEUE_KEY + ":" + "group";
    public static final String TASK_STATUS_UPDATE_QUEUE_CONSUMER =
        TASK_STATUS_UPDATE_QUEUE_KEY + ":" + "service";

    public static String getAlgorithmTaskStatusUpdateKey(Long studyId) {
        return ALGORITHM_STUDY_PREFIX + studyId + ":" + TASK_STATUS_UPDATE_SUFFIX;
    }

}
