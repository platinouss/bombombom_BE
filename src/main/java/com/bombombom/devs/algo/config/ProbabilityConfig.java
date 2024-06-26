package com.bombombom.devs.algo.config;

import com.bombombom.devs.algo.models.AlgoTag;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProbabilityConfig {

    @Value("${algo.probability.math}")
    private Double math;

    @Value("${algo.probability.dp}")
    private Double dp;

    @Value("${algo.probability.greedy}")
    private Double greedy;

    @Value("${algo.probability.impl}")
    private Double impl;

    @Value("${algo.probability.graph}")
    private Double graph;

    @Value("${algo.probability.geometry}")
    private Double geometry;

    @Value("${algo.probability.ds}")
    private Double ds;

    @Value("${algo.probability.string}")
    private Double string;

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

        double choiceSpreadStart = 0;
        for (AlgoTag tag : AlgoTag.values()) {
            tag.setChoiceSpreadStart(choiceSpreadStart);
            tag.setChoiceSpreadEnd(choiceSpreadStart + tag.getProbability());
            choiceSpreadStart = tag.getChoiceSpreadEnd();
        }
        totalProbability = math + dp + greedy + impl + graph + geometry + ds + string;
    }

}
