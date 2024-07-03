package com.bombombom.devs.client.solvedac;

import com.bombombom.devs.algo.models.AlgoTag;
import java.util.List;
import lombok.Builder;

@Builder
public class ProblemSearchParam {

    private List<String> baekjoonIds;
    private AlgoTag tag;
    private Integer numberOfProblems;
    private Integer difficultySpread;

}
