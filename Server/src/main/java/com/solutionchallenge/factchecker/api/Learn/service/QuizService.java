package com.solutionchallenge.factchecker.api.Learn.service;

import com.solutionchallenge.factchecker.api.Learn.dto.response.ChallengeQuizResponseDto;
import com.solutionchallenge.factchecker.global.exception.CustomException;
import com.solutionchallenge.factchecker.api.Learn.dto.response.DailyQuizScoreResponseDto;
import com.solutionchallenge.factchecker.api.Member.entity.Member;
import com.solutionchallenge.factchecker.api.Member.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

//Module Service
@Service
@Slf4j
public class QuizService {
    private final MemberRepository memberRepository;

    @Autowired
    public QuizService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional(readOnly = false)
    public DailyQuizScoreResponseDto updateScore(String member_id, String day, int score) {
        List<String> validDays = Arrays.asList("월", "화", "수", "목", "금", "토", "일");
        if (!validDays.contains(day)) {
            throw new CustomException("요일로 잘못된 값이 들어왔습니다 : " + day);
        }
        Member member = memberRepository.findMemberById(member_id).orElseThrow(()->new CustomException("User not found"));
        Map<String, Integer> newmap = member.getDailyScore();
        log.info("초기 값: " + member);
        int existScore = newmap.get(day);
        log.info("기존값: " + existScore + "입력값: " + score);
        if (score > existScore) {
            newmap.put(day, score);
            member.updateScore((HashMap<String, Integer>) newmap);
        }
        Member updatedUser = memberRepository.save(member);
        log.info("변경된 값: " + updatedUser);
        return new DailyQuizScoreResponseDto(updatedUser);
    }


    public ChallengeQuizResponseDto challengeDailyQuiz(String member_id) {
        Member member = memberRepository.findMemberById(member_id).orElseThrow(()->new CustomException("User not found"));

        int leftOpportunity = member.getLeft_opportunity();

        if (leftOpportunity >= 1) {
            member.consumeLeftOpportunities();
            Member updatedUser = memberRepository.save(member);
            return new ChallengeQuizResponseDto(updatedUser);
        } else {
            throw new CustomException("Daily Quiz opportunity is not remain");
        }
    }
}
