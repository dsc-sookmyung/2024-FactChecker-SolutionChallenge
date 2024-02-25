package com.solutionchallenge.factchecker.api.Learn.dto.response;

import com.solutionchallenge.factchecker.api.Member.entity.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChallengeQuizResponseDto {
    private String userId;
    private int left_opportunities;

    public ChallengeQuizResponseDto(Member member) {
        this.userId = member.getId();
        this.left_opportunities = member.getLeft_opportunity();
    }
}
