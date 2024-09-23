package com.bombombom.devs.external.global.decryption;

import com.bombombom.devs.external.encryption.service.AsymmetricEncryptionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DecryptionFilterConfig {

    private final ObjectMapper objectMapper;
    private final AsymmetricEncryptionService asymmetricEncryptionService;

    @Bean
    public FilterRegistrationBean<DecryptionFilter> decryptionFilterRegistration() {
        FilterRegistrationBean<DecryptionFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new DecryptionFilter(objectMapper, asymmetricEncryptionService));
        registrationBean.addUrlPatterns("/api/v1/auth");
        return registrationBean;
    }

}
