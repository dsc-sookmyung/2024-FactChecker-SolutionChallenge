package com.solutionchallenge.factchecker.api.Member.dto.response;

import com.solutionchallenge.factchecker.api.Member.entity.Grade;
import com.solutionchallenge.factchecker.api.Member.entity.Member;
import com.solutionchallenge.factchecker.api.Member.entity.Tier;
import lombok.Data;

@Data
public class MyProfileResponseDto {
    private String nickname;
    private Tier tier;
    private int weekly_total_read;
    private int weekly_read_goal;
    private int  weekly_total_quiz;
    private int weekly_quiz_goal;
    private int bonusScore;
/*
[ 기사수 / 단어수 ]
초급 : 5개 / 20개
중급 : 10개 / 40개
고급 : 15개 / 60개
 */
    public MyProfileResponseDto(Member member, int weekNews, int dailyScore) {
        if (member.getGrade() == Grade.BEGINNER) {
            this.weekly_read_goal = 5;
            this.weekly_quiz_goal = 20;
        } else if (member.getGrade() == Grade.INTERMEDIATE) {
            this.weekly_read_goal = 10;
            this.weekly_quiz_goal = 40;
        } else if (member.getGrade() == Grade.ADVANCED) {
            this.weekly_read_goal = 15;
            this.weekly_quiz_goal = 60;
        }
        this.nickname = member.getNickname();
        this.tier =  member.getTier();
        this.weekly_total_read = weekNews;
        this.weekly_total_quiz = dailyScore;
        this.bonusScore = member.getBonusScore();
    }
}
