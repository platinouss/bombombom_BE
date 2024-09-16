package com.bombombom.devs.external.global.decryption;

import com.bombombom.devs.external.encryption.service.EncryptionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
@RequiredArgsConstructor
public class DecryptionFilterConfig {

    private final ObjectMapper objectMapper;
    private final EncryptionService encryptionService;

    @Bean
    public FilterRegistrationBean<DecryptionFilter> decryptionFilterFilterRegistration() {
        FilterRegistrationBean<DecryptionFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new DecryptionFilter(objectMapper, encryptionService));
        registrationBean.addUrlPatterns("/api/v1/auth");
        registrationBean.setOrder(Ordered.LOWEST_PRECEDENCE);
        return registrationBean;
    }
}
