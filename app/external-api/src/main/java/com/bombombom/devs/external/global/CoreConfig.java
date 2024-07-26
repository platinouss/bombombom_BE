package com.bombombom.devs.external.global;

import com.bombombom.devs.core.util.Clock;
import com.bombombom.devs.core.util.SystemClock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CoreConfig {

    @Bean
    public Clock clock() {
        return new SystemClock();
    }


}
