package com.solutionchallenge.factchecker.api.Youtube.dto.request;

import lombok.Getter;

@Getter
public class YoutubeURLRequestDto {
    private String url;
    // 기본 생성자 추가
    public YoutubeURLRequestDto() {
        // 기본 생성자
    }
    public YoutubeURLRequestDto(String url) {
            this.url = url;
    }
}
