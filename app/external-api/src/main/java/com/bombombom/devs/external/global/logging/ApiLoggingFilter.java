package com.bombombom.devs.external.global.logging;

import com.bombombom.devs.external.global.logging.dto.ApiLogInfo;
import com.bombombom.devs.security.AppUserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
class ApiLoggingFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws IOException, ServletException {
        String requestId = Optional.ofNullable(request.getHeader("request_id"))
            .orElse(UUID.randomUUID().toString());
        MDC.put("request_id", requestId);
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        Long userId = getRequestUserId();
        long start = System.currentTimeMillis();
        try {
            filterChain.doFilter(requestWrapper, responseWrapper);
            long elapsedTime = System.currentTimeMillis() - start;
            ApiLogInfo apiLogInfo = ApiLogInfo.fromResult(requestWrapper, responseWrapper, userId,
                elapsedTime);
            log.info(objectMapper.writeValueAsString(apiLogInfo));
        } catch (Throwable e) {
            ApiLogInfo apiLogInfo = ApiLogInfo.fromResult(requestWrapper, userId);
            log.info(objectMapper.writeValueAsString(apiLogInfo));
            throw e;
        } finally {
            responseWrapper.copyBodyToResponse();
        }
        MDC.clear();
    }

    private Long getRequestUserId() {
        Long userId = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof AppUserDetails) {
                userId = ((AppUserDetails) principal).getId();
            }
        }
        return userId;
    }
}
