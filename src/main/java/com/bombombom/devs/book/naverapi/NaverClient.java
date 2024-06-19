package com.bombombom.devs.book.naverapi;

import com.bombombom.devs.book.naverapi.dto.NaverBookApiRequest;
import com.bombombom.devs.book.naverapi.dto.NaverBookApiResponse;
import com.bombombom.devs.book.naverapi.exception.ExternalApiArgumentNotValidException;
import com.bombombom.devs.book.naverapi.exception.ExternalApiException;
import com.bombombom.devs.global.util.converter.MultiValueMapConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    public NaverBookApiResponse searchBooks(NaverBookApiRequest naverBookApiRequest) {
        if (naverBookApiRequest.query().isBlank()) {
            throw new ExternalApiArgumentNotValidException("query는 공백일 수 없습니다.");
        }
        WebClient webClient = WebClient.builder().baseUrl(naverBookApiUrl).build();
        return webClient.get()
            .uri(uriBuilder -> uriBuilder.queryParams(
                MultiValueMapConverter.convert(objectMapper, naverBookApiRequest)).build())
            .header("X-Naver-Client-Id", naverClientId)
            .header("X-Naver-Client-Secret", naverClientSecret)
            .retrieve()
            .onStatus(status -> !status.is2xxSuccessful(),
                clientResponse -> Mono.just(
                    new ExternalApiException("NAVER Open API 요청에 실패했습니다."))
            )
            .bodyToMono(NaverBookApiResponse.class)
            .block();
    }
}
