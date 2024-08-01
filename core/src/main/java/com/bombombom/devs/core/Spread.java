package com.bombombom.devs.core;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class Spread {

    private final Integer left;
    private final Integer right;

    public static Spread of(Integer left, Integer right) {
        return new Spread(left, right);
    }


    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (!(o instanceof Spread pair)) {
            return false;
        }

        if (!left.equals(pair.left)) {
            return false;
        }

        return right.equals(pair.right);
    }

}
