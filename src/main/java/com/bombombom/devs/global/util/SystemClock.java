package com.bombombom.devs.global.util;

import java.time.LocalDate;
import java.util.Date;
import org.springframework.stereotype.Component;

@Component
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

}
