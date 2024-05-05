package com.solutionchallenge.factchecker.api.Member.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Map;

@Data

public class SignupRequestDto {

    @NotBlank(message = "이메일을 입력해야 합니다.")
    String id; // 이메일
    @NotBlank(message = "비밀번호를 입력해야 합니다.")
    String password;
    @NotBlank(message = "닉네임을 입력해야 합니다.")
    String nickname;
    @NotBlank(message = "난이도를 선택해야 합니다.")
    String grade;
    @NotBlank(message = "관심분야를 선택해야 합니다.")
    Map<String, String> interests;
//    String interests;
//    Set<String> interests;

//    Map<String, String> interests;
//    String picture;
//    public SignupRequestDto() {
//        this.interests = new HashMap<>();
//    }
}
