package com.bombombom.devs.study.models;

public enum StudyStatus {
    READY(Values.READY), RUNNING(Values.RUNNING), END(Values.END);

    StudyStatus(String val) {
        if (!this.name().equals(val)) {
            throw new IllegalArgumentException("Incorrect use of StudyType");
        }
    }

    public static class Values {

        public static final String READY = "READY";
        public static final String RUNNING = "RUNNING";
        public static final String END = "END";
    }
}
