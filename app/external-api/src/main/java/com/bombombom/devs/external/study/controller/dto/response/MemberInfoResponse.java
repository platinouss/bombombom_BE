package com.bombombom.devs.external.study.controller.dto.response;

import com.bombombom.devs.external.study.service.dto.result.MemberInfoResult;
import lombok.Builder;

@Builder
public record MemberInfoResponse(
    Long id,
    String username,
    String baekjoonId,
    Integer reliability
) {

    public static MemberInfoResponse fromResult(MemberInfoResult result) {
        return MemberInfoResponse.builder()
            .id(result.id())
            .username(result.username())
            .baekjoonId(result.baekjoonId())
            .reliability(result.reliability())
            .build();
    }

}
