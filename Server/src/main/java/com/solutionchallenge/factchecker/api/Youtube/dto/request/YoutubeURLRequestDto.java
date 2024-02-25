package com.solutionchallenge.factchecker.api.Youtube.dto.request;

import lombok.Getter;

@Getter
public class YoutubeURLRequestDto {
    private String url;
    public YoutubeURLRequestDto() {
    }
    public YoutubeURLRequestDto(String url) {
            this.url = url;
    }
}
