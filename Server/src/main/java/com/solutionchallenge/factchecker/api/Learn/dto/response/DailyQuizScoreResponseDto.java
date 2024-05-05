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
    // 생성자, getter, setter 등 필요한 메서드들 추가

    public DailyQuizScoreResponseDto(Member member) {
        this.userId = member.getId();
        this.dailyScore = member.getDailyScore();
    }
}
