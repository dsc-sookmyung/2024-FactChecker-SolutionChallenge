package com.solutionchallenge.factchecker.global.dto.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    //success
    SUCCESS(true, HttpStatus.OK.value(), "요청에 성공했습니다."),

    //created
    CREATED(true, HttpStatus.CREATED.value(), "요청이 생성되었습니다."),

    //common
    INVALID_INPUT_VALUE_ERROR(false, HttpStatus.BAD_REQUEST.value(), "올바르지 않은 입력값입니다."),

    //internal
    INTERNAL_SERVER_ERROR(false,HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버 내부에서 문제가 발생했습니다."),
    NOT_FOUND(false, HttpStatus.NOT_FOUND.value(), "해당 정보는 존재하지 않습니다."),
    UNAUTHORIZED(false, HttpStatus.UNAUTHORIZED.value(), "권한이 없습니다."),
    S3_REGISTER_IMAGE_FAILURE_ERROR(false, HttpStatus.BAD_REQUEST.value(), "s3 이미지 저장 중 문제가 발생했습니다."),

    //auth
    EMAIL_EXISTS_ERROR(false, HttpStatus.BAD_REQUEST.value(), "이미 존재하는 이메일입니다."),
    INVALID_EMAIL_ERROR(false, HttpStatus.BAD_REQUEST.value(), "존재하지 않는 이메일 정보입니다."),
    INVALID_PASSWORD_ERROR(false, HttpStatus.BAD_REQUEST.value(), "비밀번호를 확인해주세요. 카카오 계정이라면 카카오 로그인으로 시도해주세요."),
    INVALID_ACCESS_TOKEN_ERROR(false, HttpStatus.BAD_REQUEST.value(), "AccessToken 정보를 찾을 수 없습니다."),
    INVALID_REFRESH_TOKEN_ERROR(false, HttpStatus.BAD_REQUEST.value(), "RefreshToken 정보를 찾을 수 없습니다."),
    USERNAME_EXISTS_ERROR(false, HttpStatus.BAD_REQUEST.value(), "이미 존재하는 닉네임입니다."),

    //manage
    UNSUPPORTED_TRACK_ERROR(false, HttpStatus.BAD_REQUEST.value(), "지원되지 않는 트랙입니다."),
    MISMATCHED_YEAR_ERROR(false, HttpStatus.BAD_REQUEST.value(), "해당 년도와 일치하지 않습니다."),
    ALREADY_EXISTS_QUESTION_NUMBER_ERROR(false, HttpStatus.BAD_REQUEST.value(), "이미 해당 년도, 해당 트랙에 존재하는 문항 번호입니다."),
    NOT_FOUND_QUESTION_ERROR(false, HttpStatus.BAD_REQUEST.value(), "해당 문항을 찾을 수 없습니다."),
    MISMATCHED_TRACK_ERROR(false, HttpStatus.BAD_REQUEST.value(), "해당 트랙과 일치하지 않습니다."),
    ;

    private Boolean isSuccess;
    private int code;
    private String message;

    ErrorCode(Boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }

}
