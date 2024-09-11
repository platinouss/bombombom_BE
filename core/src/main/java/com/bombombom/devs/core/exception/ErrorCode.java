package com.bombombom.devs.core.exception;

import static com.bombombom.devs.core.exception.StatusCode.BAD_REQUEST;
import static com.bombombom.devs.core.exception.StatusCode.CONFLICT;
import static com.bombombom.devs.core.exception.StatusCode.FORBIDDEN;
import static com.bombombom.devs.core.exception.StatusCode.INTERNAL_SERVER_ERROR;
import static com.bombombom.devs.core.exception.StatusCode.NOT_ACCEPTABLE;
import static com.bombombom.devs.core.exception.StatusCode.NOT_FOUND;
import static com.bombombom.devs.core.exception.StatusCode.UNAUTHORIZED;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // BAD_REQUEST 400
    INVALID_INPUT(BAD_REQUEST, 40000, "잘못된 요청입니다."),
    NOT_NEXT_ROUND_IDX(BAD_REQUEST, 40001, "다음 라운드의 인덱스가 아닙니다."),


    // UNAUTHORIZED 401
    INVALID_USER(UNAUTHORIZED, 40100, "유효하지 않은 유저입니다."),
    INVALID_TOKEN(UNAUTHORIZED, 40101, "유효하지 않은 토큰입니다."),

    // FORBIDDEN 403
    ONLY_LEADER_ALLOWED(FORBIDDEN, 40300, "스터디 리더가 아닙니다."),
    ONLY_MEMBER_ALLOWED(FORBIDDEN, 40301, "스터디 멤버가 아닙니다."),

    // NOT_FOUND 404
    BOOK_NOT_FOUND(NOT_FOUND, 40400, "해당 서적이 존재하지 않습니다."),
    USER_NOT_FOUND(NOT_FOUND, 40401, "유저를 찾을 수 없습니다."),
    TAG_NOT_FOUND(NOT_FOUND, 40402, "알고리즘 태그를 찾을 수 없습니다."),
    STUDY_NOT_FOUND(NOT_FOUND, 40403, "스터디를 찾을 수 없습니다."),
    ROUND_NOT_FOUND(NOT_FOUND, 40404, "회차정보를 찾을 수 없습니다."),
    ASSIGNMENT_NOT_FOUND(NOT_FOUND, 40405, "과제정보를 찾을 수 없습니다."),
    PROBLEM_NOT_FOUND(NOT_FOUND, 40406, "문제를 찾을 수 없습니다."),
    SOLVE_HISTORY_NOT_FOUND(NOT_FOUND, 40406, "풀이 여부를 알 수 없습니다."),
    USER_ASSIGNMENT_NOT_FOUND(NOT_FOUND, 40407, "유저에 과제를 할당한 정보를 찾을 수 없습니다."),
    NEXT_ROUND_NOT_FOUND(NOT_FOUND, 40408, "다음 회차가 존재하지 않습니다."),

    // NOT_ACCEPTABLE 406
    STUDY_STARTED(NOT_ACCEPTABLE, 40600, "스터디가 시작하였습니다."),
    PROBLEM_NOT_SOLVED(NOT_ACCEPTABLE, 40601, "문제를 먼저 해결해야합니다."),
    WRONG_STUDY_TYPE(NOT_ACCEPTABLE, 40602, "해당 스터디 타입은 지원하지 않습니다."),
    NOT_NEXT_ROUND_ASSIGNMENT(NOT_ACCEPTABLE, 40603, "다음 회차의 과제가 아닙니다."),
    NOT_ENOUGH_MONEY(NOT_ACCEPTABLE, 40604, "돈이 부족합니다."),
    NEGATIVE_AMOUNT(NOT_ACCEPTABLE, 40605, "음수는 허용되지 않습니다."),
    STUDY_ENDED(NOT_ACCEPTABLE, 40606, "스터디가 종료되었습니다."),
    STUDY_IS_FULL(NOT_ACCEPTABLE, 40607, "스터디가 만원입니다."),
    NOT_ENOUGH_RELIABILITY(NOT_ACCEPTABLE, 40608, "신뢰도가 부족합니다."),
    ALREADY_JOINED(NOT_ACCEPTABLE, 40609, "이미 가입한 스터디입니다."),
    VIDEO_ASSIGNMENT_ID_NOT_MATCH(NOT_ACCEPTABLE, 40610, "영상의 과제 ID가 일치하지 않습니다."),
    PROBLEM_ASSIGNMENT_ID_NOT_MATCH(NOT_ACCEPTABLE, 40611, "문제의 과제 ID가 일치하지 않습니다."),
    VOTING_PROCESS_NOT_READY(NOT_ACCEPTABLE, 40612, "투표가 준비 중이 아닙니다."),
    VOTING_PROCESS_NOT_ONGOING(NOT_ACCEPTABLE, 40613, "투표가 진행 중이 아닙니다."),

    // CONFLICT 409
    DUPLICATED_USERNAME(CONFLICT, 40900, "이미 사용중인 유저 이름입니다."),
    DUPLICATED_ASSIGNMENT_ID(CONFLICT, 40901, "과제 ID가 중복됩니다."),

    URL_PARAM_CONVERT_FAIL(INTERNAL_SERVER_ERROR, 50000, "URL 파라미터 변환 중 에러가 발생했습니다."),
    NAVER_BOOK_API_FAIL(INTERNAL_SERVER_ERROR, 50001, "Naver API 호출 실패"),
    INCORRECT_STUDY_TYPE(INTERNAL_SERVER_ERROR, 50002, "올바르지 않은 스터디 타입"),
    JSON_CONVERSION_FAIL(INTERNAL_SERVER_ERROR, 50003, "JSON 변환에 실패했습니다."),
    UNEXPECTED_EXCEPTION(INTERNAL_SERVER_ERROR, 50004, "예기치 못한 에러가 발생했습니다.");


    private final StatusCode statusCode;
    private final int code;
    private final String message;

}
