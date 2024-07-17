package com.bombombom.devs.job;

import com.bombombom.devs.algo.models.AlgoTag;
import com.bombombom.devs.algo.models.AlgorithmProblem;
import com.bombombom.devs.solvedac.dto.ProblemListResponse;
import com.bombombom.devs.solvedac.dto.ProblemResponse;
import com.bombombom.devs.solvedac.dto.ProblemTag;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AlgorithmProblemConverter {

    private static final String BOJ_URL = "https://www.acmicpc.net/problem/";

    public List<AlgorithmProblem> convert(ProblemListResponse response) {
        return response.items().stream()
            .map(this::convert)
            .toList();
    }

    public AlgorithmProblem convert(ProblemResponse response) {
        return AlgorithmProblem.builder()
            .refId(response.problemId())
            .link(BOJ_URL + response.problemId())
            .title(response.titleKo())
            .difficulty(response.level())
            .tag(convertTag(response))
            .build();
    }

    private AlgoTag convertTag(ProblemResponse response) {
        List<String> tagValues = AlgoTag.getTagNames();
        ProblemTag problemTag = response.tags().stream().filter(
            tag -> tagValues.contains(tag.key().toUpperCase())
        ).findFirst().orElseThrow(() -> {
            log.error("Tag not found: {}", response.tags());
            return new IllegalStateException("Tag not found");
        });
        return AlgoTag.valueOf(problemTag.key().toUpperCase());
    }

}
