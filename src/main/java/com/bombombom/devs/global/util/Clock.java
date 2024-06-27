package com.bombombom.devs.global.util;

import java.time.LocalDate;
import java.util.Date;

public interface Clock {

    Date calculateFutureDateFromNow(long ms);

    LocalDate today();

}
