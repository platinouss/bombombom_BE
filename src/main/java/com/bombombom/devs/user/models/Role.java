package com.bombombom.devs.user.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    TEAM_MEMBER("ROLE_USER"),
    PART_LEADER("ROLE_ADMIN");

    private final String name;
}
