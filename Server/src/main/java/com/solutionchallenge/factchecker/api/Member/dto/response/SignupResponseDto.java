package com.solutionchallenge.factchecker.api.Member.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
public class SignupResponseDto {
    String id;
    String nickname;
    String grade;
    Map<String, String> interests;


    @Builder
    public SignupResponseDto(String id, String nickname, String grade, Map<String , String> interests) {
        this.id = id;
        this.nickname = nickname;
        this.grade = grade;
        this.interests = interests;
    }
}
