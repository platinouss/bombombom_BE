package com.bombombom.devs.algo.models;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum AlgoTag {
    MATH,
    DP,
    GREEDY,
    IMPL,
    GRAPH,
    GEOMETRY,
    DS,
    STRING;

    @Setter
    private Double probability;
    @Setter
    private Double choiceSpreadStart;
    @Setter
    private Double choiceSpreadEnd;

    public boolean isInRange(double rand) {
        return this.choiceSpreadStart <= rand && rand < this.choiceSpreadEnd;
    }
}
