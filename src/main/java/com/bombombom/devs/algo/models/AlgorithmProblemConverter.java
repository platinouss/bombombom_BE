package com.bombombom.devs.algo.models;

import com.bombombom.devs.client.solvedac.dto.ProblemListResponse;
import com.bombombom.devs.client.solvedac.dto.ProblemResponse;
import java.util.List;

public class AlgorithmProblemConverter {

    private static final String BOJ_URL = "https://www.acmicpc.net/problem/";

    public static List<AlgorithmProblem> convert(ProblemListResponse response) {
        return response.items().stream()
            .map(AlgorithmProblemConverter::convert)
            .toList();
    }

    public static AlgorithmProblem convert(ProblemResponse response) {
        return AlgorithmProblem.builder()
            .refId(response.problemId())
            .link(BOJ_URL + response.problemId())
            .title(response.titleKo())
            .difficulty(response.level())
            .tag(AlgoTag.valueOf(response.tags().getFirst().key()))
            .build();
    }

}
