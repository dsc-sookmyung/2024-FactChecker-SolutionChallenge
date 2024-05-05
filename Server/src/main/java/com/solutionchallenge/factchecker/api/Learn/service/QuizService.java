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

        // 멤버가 존재할 경우
        int leftOpportunity = member.getLeft_opportunity();

        if (leftOpportunity >= 1) {
            // left_opportunity가 1 이상인 경우
            member.consumeLeftOpportunities(); // 기회 1 소진
            Member updatedUser = memberRepository.save(member);
            // Daily Quiz에 대한 로직 수행 및 결과를 DailyQuizScoreResponseDto에 담아 반환
            return new ChallengeQuizResponseDto(updatedUser);
        } else {
            // left_opportunCity가 0 이하인 경우
            throw new CustomException("Daily Quiz opportunity is not remain");
        }
    }
}
