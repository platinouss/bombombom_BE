package com.bombombom.devs.core.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public class SystemClock implements Clock {

    @Override
    public Date calculateFutureDateFromNow(long ms) {
        Date now = new Date();
        long futureTimeInMillis = now.getTime() + ms;
        return new Date(futureTimeInMillis);
    }

    @Override
    public LocalDate today() {
        return LocalDate.now();
    }

    @Override
    public LocalDateTime now() {
        return LocalDateTime.now();
    }

}
