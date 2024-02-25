package com.solutionchallenge.factchecker.api.Youtube.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class MLResponseDto {

    @JsonProperty("yt_title")
    private String yt_title;
    @JsonProperty("upload_date")
    private String uploadDate;
    @JsonProperty("keyword")
    private String keyword;

    @JsonProperty("curr_youtube_news")
    private List<RelatedNewsDto> currYoutubeNews;

    @JsonProperty("rel_youtube_news")
    private List<RelatedNewsDto> relYoutubeNews;
}



