package com.solutionchallenge.factchecker.api.Interests.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MLResponseDto {

    @JsonProperty("title")
    private String title;
    @JsonProperty("article")
    private String article;
    @JsonProperty("date")
    private String date;
    @JsonProperty("credibility")
    private float credibility;
    @JsonProperty("section")
    private int section;

}



