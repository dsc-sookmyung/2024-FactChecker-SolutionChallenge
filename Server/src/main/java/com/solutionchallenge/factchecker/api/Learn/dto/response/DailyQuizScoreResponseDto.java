package com.solutionchallenge.factchecker.api.Learn.dto.response;

import com.solutionchallenge.factchecker.api.Member.entity.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyQuizScoreResponseDto {
    private String userId;
    private Map<String, Integer> dailyScore;

    public DailyQuizScoreResponseDto(Member member) {
        this.userId = member.getId();
        this.dailyScore = member.getDailyScore();
    }
}
