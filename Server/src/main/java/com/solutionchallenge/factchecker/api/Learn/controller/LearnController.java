package com.solutionchallenge.factchecker.api.Learn.controller;

import com.solutionchallenge.factchecker.api.Learn.dto.request.ArticleWordRequestDto;
import com.solutionchallenge.factchecker.api.Learn.dto.response.ArticleWordResponse;
import com.solutionchallenge.factchecker.api.Learn.dto.response.ChallengeQuizResponseDto;
import com.solutionchallenge.factchecker.api.Learn.service.WordService;
import com.solutionchallenge.factchecker.api.Member.entity.UserDetailsImpl;
import com.solutionchallenge.factchecker.api.Learn.dto.response.DailyQuizScoreResponseDto;
import com.solutionchallenge.factchecker.api.Learn.dto.response.WordResponseDto;
import com.solutionchallenge.factchecker.api.Learn.dto.request.DailyQuizRequestDto;
import com.solutionchallenge.factchecker.api.Learn.service.LearnService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name= "Learn Controller", description = "단어퀴즈 페이지 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class LearnController {
    private final LearnService learnService;
    private final WordService wordService;
    @PostMapping("/learn/words")
    public ResponseEntity<Map<String, Object>> saveWords(
            @RequestBody ArticleWordRequestDto wordsRequestDto,
            @RequestHeader(name = "ACCESS_TOKEN", required = false) String ACCESS_TOKEN,
            @RequestHeader(name = "REFRESH_TOKEN", required = false) String REFRESH_TOKEN
    ) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetailsImpl userDetails = (UserDetailsImpl) principal;
        String memberId = userDetails.getUsername();

        List<ArticleWordResponse> savedWords = learnService.saveWord(memberId, wordsRequestDto);

        Map<String, Object> response = new HashMap<>();
        response.put("isSuccess", true);
        response.put("code", HttpStatus.CREATED.value());
        response.put("message", "요청에 성공했습니다.");
        response.put("data", savedWords);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "단어장 - 단어 리스트 조회", description = "단어장에 올릴 단어들을 조회합니다. 이때 생성일 기준 최신순으로 정렬해서 가져옵니다.",
            responses = {@ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(array = @ArraySchema(schema = @Schema(implementation = WordResponseDto.class))))})
    @GetMapping("/words")
    public ResponseEntity<?> returnWords(
            @RequestHeader(name = "ACCESS_TOKEN", required = false) String ACCESS_TOKEN,
            @RequestHeader(name = "REFRESH_TOKEN", required = false) String REFRESH_TOKEN){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetailsImpl userDetails = (UserDetailsImpl)principal;

        List<WordResponseDto> list = learnService.getWordList(userDetails.getUsername());
        return ResponseEntity.ok().body(list);
    }

    @Operation(summary = "단어장 - 단어 knowstatus 정보 수정", description = "단어장에서 토글버튼을 누르면 받아온 값에 따라 true-> false/ false -> true 전환",
            responses = {@ApiResponse(responseCode = "200", description = "수정 성공", content = @Content(schema = @Schema(implementation = WordResponseDto.class)))})
    @PatchMapping("/wordlist/{wordId}")
    public ResponseEntity<?> setWordKnowStatus(@PathVariable Long wordId,
                                               @RequestHeader(name = "ACCESS_TOKEN", required = false) String ACCESS_TOKEN,
                                               @RequestHeader(name = "REFRESH_TOKEN", required = false) String REFRESH_TOKEN) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetailsImpl userDetails = (UserDetailsImpl)principal;

        WordResponseDto dto = learnService.updateWord(wordId, userDetails.getUsername());
        return ResponseEntity.ok().body(dto);
    }
    @Operation(summary = "어휘학습 - flip card", description = "모르는 단어 리스트를 랜덤으로 가져온다 -> flip card로 제공",
            responses = {@ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(array = @ArraySchema(schema = @Schema(implementation = WordResponseDto.class))))})
    @GetMapping("/study/flip-cards/word")
    public ResponseEntity<?> returnFlipCardWords(
            @RequestHeader(name = "ACCESS_TOKEN", required = false) String ACCESS_TOKEN,
            @RequestHeader(name = "REFRESH_TOKEN", required = false) String REFRESH_TOKEN){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        UserDetailsImpl userDetails = (UserDetailsImpl)principal;
        log.info("[flip card 테스트] {} 회원님이 존재합니다", userDetails.getUsername());
        List<WordResponseDto> list  = learnService.getUnknownWordList(userDetails.getUsername());
        return ResponseEntity.ok().body(list);
    }

    @Operation(summary = "데일리 퀴즈 - 퀴즈점수 저장", description = "퀴즈 점수를 저장한다.",
            responses = {@ApiResponse(responseCode = "200", description = "일일 점수가 성공적으로 저장되었습니다.",
                            content = @Content(schema = @Schema(implementation = DailyQuizScoreResponseDto.class)))
            })
    @PostMapping("/study/daily-quiz/score")
    public ResponseEntity<?> setScore(
            @RequestBody DailyQuizRequestDto dailyQuizRequestDto,
            @RequestHeader(name = "ACCESS_TOKEN", required = false) String ACCESS_TOKEN,
            @RequestHeader(name = "REFRESH_TOKEN", required = false) String REFRESH_TOKEN
    ) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetailsImpl userDetails = (UserDetailsImpl) principal;

        DailyQuizScoreResponseDto dto = learnService.updateScore(userDetails.getUsername(), dailyQuizRequestDto.getDay(), dailyQuizRequestDto.getScore());
        return ResponseEntity.ok().body(dto);
    }

    @Operation(summary = "데일리 퀴즈 - 퀴즈 도전", description = "남은 데일리퀴즈 도전기회가 있는지 체크 후, 가능한 경우 기회를 한개 소진하며, 불가능한경우 예외를 반환한다.",
            responses = {@ApiResponse(responseCode = "200", description = "데일리 퀴즈에 도전할 수 있습니다.",
                    content = @Content(schema = @Schema(implementation = ChallengeQuizResponseDto.class)))
            })
    @PatchMapping("/study/daily-quiz/try")
    public ResponseEntity<?> ChallengeDailyQuiz(
            @RequestHeader(name = "ACCESS_TOKEN", required = false) String ACCESS_TOKEN,
            @RequestHeader(name = "REFRESH_TOKEN", required = false) String REFRESH_TOKEN
    ) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetailsImpl userDetails = (UserDetailsImpl) principal;

        ChallengeQuizResponseDto dto = learnService.challengeDailyQuiz(userDetails.getUsername());
        return ResponseEntity.ok().body(dto);
    }



}
