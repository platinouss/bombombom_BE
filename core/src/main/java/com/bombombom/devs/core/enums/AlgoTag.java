
package com.bombombom.devs.core.enums;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
public enum AlgoTag {
    MATH,
    DP,
    GREEDY,
    IMPLEMENTATION,
    GRAPHS,
    GEOMETRY,
    DATA_STRUCTURES,
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

    public static List<String> getTagNames() {
        return List.of(
            "MATH", "DP", "GREEDY", "IMPLEMENTATION", "GRAPHS", "GEOMETRY", "DATA_STRUCTURES",
            "STRING");
    }
}
