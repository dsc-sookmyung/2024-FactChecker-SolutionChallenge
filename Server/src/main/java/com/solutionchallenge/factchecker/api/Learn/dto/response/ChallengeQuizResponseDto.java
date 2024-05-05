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
    // 생성자, getter, setter 등 필요한 메서드들 추가

    public ChallengeQuizResponseDto(Member member) {
        this.userId = member.getId();
        this.left_opportunities = member.getLeft_opportunity();
    }
}
