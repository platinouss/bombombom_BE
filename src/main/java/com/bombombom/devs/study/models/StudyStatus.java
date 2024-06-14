package com.bombombom.devs.study.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum StudyStatus {
    READY("READY"), RUNNING("RUNNING"), END("END");

    private final String name;
}
