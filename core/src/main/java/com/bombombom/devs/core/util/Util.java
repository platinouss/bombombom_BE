package com.bombombom.devs.core.util;

import com.bombombom.devs.core.exception.ErrorCode;
import com.bombombom.devs.core.exception.ServerInternalException;
import java.util.Collection;
import java.util.List;

public class Util {

    public static int ensureRange(int value, int min, int max) {
        return Math.min(Math.max(value, min), max);
    }


    public static <E> E getRandom(Collection<E> e) {
        if (e instanceof List<E> list) {
            return list.get((int) (e.size() * Math.random()));
        }

        return e.stream()
            .skip((int) (e.size() * Math.random()))
            .findFirst()
            .orElseThrow(
                () -> new ServerInternalException(ErrorCode.INDEX_OUT_OF_RANGE_IN_GET_RANDOM));
    }
}
