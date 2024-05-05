package com.solutionchallenge.factchecker.api.Learn.dto.request;

import lombok.Getter;

@Getter
public class DailyQuizRequestDto {
    private String day; //예시: "월", "화" , "수"
    private int score;
    // 기본 생성자 추가
    public DailyQuizRequestDto() {
    }
    public DailyQuizRequestDto( String day, int score) {
        this.day = day;
        this.score = score;
    }
}
