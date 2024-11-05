package com.bombombom.devs.external.global.logging.dto;

import com.bombombom.devs.core.exception.ErrorCode;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import lombok.Builder;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Builder
public record ApiLogInfo(
    @JsonProperty(value = "http_method") String httpMethod,
    String uri,
    @JsonProperty(value = "user_id") Long userId,
    @JsonProperty(value = "request_header") Map<String, String> requestHeader,
    @JsonProperty(value = "response_header") Map<String, String> responseHeader,
    @JsonProperty(value = "request_body") String requestBody,
    @JsonProperty(value = "response_body") String responseBody,
    @JsonProperty(value = "client_ip") String clientIp,
    @JsonProperty(value = "elapsed_time") long elapsedTime
) {

    public static ApiLogInfo fromResult(ContentCachingRequestWrapper request,
        ContentCachingResponseWrapper response, Long userId, long elapsedTime) throws IOException {
        String requestBody = new String(request.getContentAsByteArray(), StandardCharsets.UTF_8);
        String responseBody = new String(response.getContentAsByteArray(),
            StandardCharsets.UTF_8);
        return ApiLogInfo.builder()
            .httpMethod(request.getMethod())
            .uri(request.getRequestURI())
            .userId(userId)
            .requestHeader(getRequestHeader(request))
            .responseHeader(getResponseHeader(response))
            .requestBody(requestBody)
            .responseBody(responseBody)
            .clientIp(getClientIp(request))
            .elapsedTime(elapsedTime)
            .build();
    }

    public static ApiLogInfo fromResult(ContentCachingRequestWrapper request, Long userId)
        throws IOException {
        String requestBody = new String(request.getContentAsByteArray(), StandardCharsets.UTF_8);
        return ApiLogInfo.builder()
            .httpMethod(request.getMethod())
            .uri(request.getRequestURI())
            .userId(userId)
            .requestHeader(getRequestHeader(request))
            .requestBody(requestBody)
            .responseBody(String.valueOf(ErrorCode.UNEXPECTED_EXCEPTION))
            .clientIp(getClientIp(request))
            .build();
    }

    private static Map<String, String> getRequestHeader(HttpServletRequest request) {
        Map<String, String> requestHeaders = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            requestHeaders.put(headerName, request.getHeader(headerName));
        }
        return requestHeaders;
    }

    private static Map<String, String> getResponseHeader(HttpServletResponse response) {
        Map<String, String> responseHeaders = new HashMap<>();
        Collection<String> headerNames = response.getHeaderNames();
        for (String headerName : headerNames) {
            responseHeaders.put(headerName, response.getHeader(headerName));
        }
        return responseHeaders;
    }

    private static String getClientIp(HttpServletRequest request) {
        String clientIp;
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader != null && !xForwardedForHeader.isEmpty()) {
            clientIp = xForwardedForHeader.split(",")[0];
        } else {
            clientIp = request.getRemoteAddr();
        }
        return clientIp;
    }

}
