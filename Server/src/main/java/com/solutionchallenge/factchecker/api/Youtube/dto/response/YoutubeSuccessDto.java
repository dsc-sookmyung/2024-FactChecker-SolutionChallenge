package com.solutionchallenge.factchecker.api.Youtube.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class YoutubeSuccessDto {
    private String memberId;
    private String url;

    public YoutubeSuccessDto(String memberId, String url) {
        this.memberId = memberId;
        this.url = url;
    }
}