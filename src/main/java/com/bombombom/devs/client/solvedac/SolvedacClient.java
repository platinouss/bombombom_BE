package com.bombombom.devs.client.solvedac;

import com.bombombom.devs.client.solvedac.dto.ProblemListResponse;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@Component
public class SolvedacClient {

    private static final String BASE_URL = "https://solved.ac/api/v3";
    private static final String SEARCH_PROBLEM_PATH = "/search/problem";

    public ProblemListResponse getUnResolvedProblems(List<String> baekjoonIds) {
        CompletableFuture<ProblemListResponse> completableFuture = new CompletableFuture<>();
        WebClient webClient = WebClient.builder().baseUrl(BASE_URL).build();

        String queryParam = makeGetUnSolvedProblemsQueryParams(baekjoonIds);
        Mono<ProblemListResponse> mono = webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path(SEARCH_PROBLEM_PATH)
                .queryParam("query", queryParam)
                .build()
            )
            .retrieve()
            .bodyToMono(ProblemListResponse.class);

        mono.subscribe(
            response -> {
                log.info("getUnResolvedProblems() Response: {}", response);
                completableFuture.complete(response);
            },
            error -> {
                log.error("Error during getUnResolvedProblems: " + error.getMessage());
            }
        );
        return completableFuture.join();
    }

    private String makeGetUnSolvedProblemsQueryParams(List<String> baekjoonIds) {
        String prefix = "s!@";
        StringJoiner joiner = new StringJoiner(" " + prefix, prefix, "");
        for (String id : baekjoonIds) {
            joiner.add(id);
        }
        return joiner.toString();
    }

}
