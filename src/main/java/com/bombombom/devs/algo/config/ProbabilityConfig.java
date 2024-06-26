package com.bombombom.devs.algo.config;

import com.bombombom.devs.algo.models.AlgoTag;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProbabilityConfig {

    @Value("${problem.algo.probability.math}")
    private Double math;

    @Value("${problem.algo.probability.dp}")
    private Double dp;

    @Value("${problem.algo.probability.greedy}")
    private Double greedy;

    @Value("${problem.algo.probability.impl}")
    private Double impl;

    @Value("${problem.algo.probability.graph}")
    private Double graph;

    @Value("${problem.algo.probability.geometry}")
    private Double geometry;

    @Value("${problem.algo.probability.ds}")
    private Double ds;

    @Value("${problem.algo.probability.string}")
    private Double string;

    @Value("${problem.algo.probability.gap}")
    private Double gap;

    public static Double totalProbability;

    @PostConstruct
    public void init() {
        AlgoTag.MATH.setProbability(math);
        AlgoTag.DP.setProbability(dp);
        AlgoTag.GREEDY.setProbability(greedy);
        AlgoTag.IMPL.setProbability(impl);
        AlgoTag.GRAPH.setProbability(graph);
        AlgoTag.GEOMETRY.setProbability(geometry);
        AlgoTag.DS.setProbability(ds);
        AlgoTag.STRING.setProbability(string);
        AlgoTag.GAP.setProbability(gap);

        double choiceSpreadStart = 0;
        for (AlgoTag tag : AlgoTag.values()) {
            tag.setChoiceSpreadStart(choiceSpreadStart);
            tag.setChoiceSpreadEnd(choiceSpreadStart + tag.getProbability());
            choiceSpreadStart = tag.getChoiceSpreadEnd();
        }
        totalProbability = math + dp + greedy + impl + graph + geometry + ds + string + gap;
    }

}
