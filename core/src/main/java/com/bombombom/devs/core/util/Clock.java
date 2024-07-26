package com.bombombom.devs.core.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public interface Clock {

    Date calculateFutureDateFromNow(long ms);

    LocalDate today();

    LocalDateTime now();

}
