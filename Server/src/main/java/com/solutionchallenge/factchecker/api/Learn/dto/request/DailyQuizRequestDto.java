package com.solutionchallenge.factchecker.api.Learn.dto.request;

import lombok.Getter;

@Getter
public class DailyQuizRequestDto {
    private String day;
    private int score;
    public DailyQuizRequestDto() {
    }
    public DailyQuizRequestDto( String day, int score) {
        this.day = day;
        this.score = score;
    }
}
