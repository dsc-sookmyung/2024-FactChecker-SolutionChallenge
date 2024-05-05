package com.solutionchallenge.factchecker.api.Member.dto.response;

import lombok.Builder;
import lombok.Data;


@Data

public class LoginResponseDto {

    String id;
    String nickname;
//    Map<String, String> interests;


    @Builder
    public LoginResponseDto(
            String id, String nickname) {
        this.id = id;
        this.nickname = nickname;


    }
}