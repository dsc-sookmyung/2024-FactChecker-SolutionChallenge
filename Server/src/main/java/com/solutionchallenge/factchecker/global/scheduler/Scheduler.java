package com.solutionchallenge.factchecker.global.scheduler;

import com.solutionchallenge.factchecker.api.Interests.service.InterestService;
import com.solutionchallenge.factchecker.api.Member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class Scheduler {
    private final MemberService memberService;
    private final InterestService interestService;

    // 매주 월요일 자정에 weekNews와 dailyScore 초기화
    @Scheduled(cron = "0 0 0 * * MON", zone = "Asia/Seoul")
    public void resetWeeklyData() {
        log.info("주간 뉴스 조회 및 데일리 퀴즈 점수 초기화 작업을 시작합니다.");
        memberService.resetWeeklyData();
    }

    // 매일 자정에 left_opportunities 초기화
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public void resetDailyOpportunities() {
        log.info("데일리 퀴즈 도전 기회 초기화 작업을 시작합니다.");
        memberService.resetDailyOpportunities();
    }

    // 정시에 3시간마다 실행
    @Scheduled(cron = "0 0 */3 * * *", zone = "Asia/Seoul")
    public void updateArticles() {
        log.info("기사 업데이트 작업을 시작합니다.");
        interestService.updateArticles();
    }
}
