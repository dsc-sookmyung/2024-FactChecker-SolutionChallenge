package com.solutionchallenge.factchecker.api.Member.dto.response;
import lombok.Data;

@Data
public class TokenResponseDto {
    private String accessToken;
    private String refreshToken;

    public TokenResponseDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
