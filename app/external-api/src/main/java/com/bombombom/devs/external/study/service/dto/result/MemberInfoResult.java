package com.bombombom.devs.external.study.service.dto.result;

import com.bombombom.devs.user.model.User;
import lombok.Builder;

@Builder
public record MemberInfoResult(
    Long id,
    String username,
    String baekjoonId,
    Integer reliability
) {

    public static MemberInfoResult fromEntity(User user) {
        return MemberInfoResult.builder()
            .id(user.getId())
            .username(user.getUsername())
            .baekjoonId(user.getBaekjoon())
            .reliability(user.getReliability())
            .build();
    }

}
