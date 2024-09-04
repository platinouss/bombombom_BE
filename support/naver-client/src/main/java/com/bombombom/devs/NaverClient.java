package com.bombombom.devs;

import com.bombombom.devs.core.exception.ErrorCode;
import com.bombombom.devs.core.exception.ExternalApiException;
import com.bombombom.devs.dto.NaverBookApiQuery;
import com.bombombom.devs.dto.NaverBookApiResult;
import com.bombombom.devs.util.converter.MultiValueMapConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class NaverClient {

    private final ObjectMapper objectMapper;

    @Value("${naver.client.id}")
    private String naverClientId;

    @Value("${naver.client.secret}")
    private String naverClientSecret;

    @Value("${naver.url.search.book}")
    private String naverBookApiUrl;

    public NaverBookApiResult requestBookApi(NaverBookApiQuery naverBookApiQuery, String apiUrl,
        ObjectMapper objectMapper) {
        WebClient webClient = WebClient.builder().baseUrl(apiUrl).build();
        return webClient.get()
            .uri(uriBuilder -> uriBuilder.queryParams(
                MultiValueMapConverter.convert(objectMapper, naverBookApiQuery)).build())
            .header("X-Naver-Client-Id", naverClientId)
            .header("X-Naver-Client-Secret", naverClientSecret)
            .retrieve()
            .onStatus(status -> !status.is2xxSuccessful(),
                clientResponse -> Mono.just(
                    new ExternalApiException(ErrorCode.NAVER_BOOK_API_FAIL,
                        Map.of(
                            "StatusCode", clientResponse.statusCode().toString(),
                            "Body", clientResponse.releaseBody().toString()
                        )
                    ))
            )
            .bodyToMono(NaverBookApiResult.class)
            .block();
    }

    public NaverBookApiResult searchBooks(NaverBookApiQuery naverBookApiQuery) {
        return requestBookApi(naverBookApiQuery, naverBookApiUrl, objectMapper);
    }
}
