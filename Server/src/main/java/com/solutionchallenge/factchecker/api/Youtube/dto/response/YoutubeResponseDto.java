package com.solutionchallenge.factchecker.api.Youtube.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class YoutubeResponseDto {
    private Long id;
    private String yt_title;
    private String url;
    private String keyword;
    private String upload_date;
    private final boolean show= false;
    private final boolean loadingStatus=false;
    private List<RelatedNewsDto> curr_youtube_news;
    private List<RelatedNewsDto> rel_youtube_news;

    public YoutubeResponseDto(Long id, String title, String url, String keyword, String uploadDate, List<RelatedNewsDto> curr_youtube_news, List<RelatedNewsDto> rel_youtube_news) {
        this.id = id;
        this.yt_title = title;
        this.url = url;
        this.keyword = keyword;
        this.upload_date = uploadDate;
        this.curr_youtube_news = curr_youtube_news;
        this.rel_youtube_news  = rel_youtube_news;
    }
}
