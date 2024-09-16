package com.bombombom.devs.external.global.decryption;

import com.bombombom.devs.external.encryption.service.EncryptionService;
import com.bombombom.devs.external.global.decryption.dto.EncryptedRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DecryptionFilter implements Filter {

    private final ObjectMapper objectMapper;
    private final EncryptionService encryptionService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
        FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        // TODO: 더 좋은 방식으로 변경 필요
        if (!httpRequest.getRequestURI().startsWith("/api/v1/auth")) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        // TODO: 특정 header가 있다면, 해당 header를 복호화 해서, 해당 데이터에 있는 private key로 복호화하는 분기 추가
        DecryptedRequestWrapper requestWrapper = new DecryptedRequestWrapper(httpRequest);
        EncryptedRequest encryptedRequest = objectMapper.readValue(requestWrapper.getRequestBody(),
            EncryptedRequest.class);
        try {
            String decryptedData = encryptionService.decryptData(encryptedRequest.id(),
                encryptedRequest.version(), encryptedRequest.encryptedData());
            requestWrapper.setRequestBody(decryptedData);
            filterChain.doFilter(requestWrapper, servletResponse);
        } catch (Exception e) {
            log.error("Failed to decrypt request body. Error details: ", e);
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}
