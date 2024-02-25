package com.solutionchallenge.factchecker.api.Interests.controller;

import com.solutionchallenge.factchecker.api.Interests.dto.request.SelectedInterestsRequestDto;
import com.solutionchallenge.factchecker.api.Interests.dto.response.InterestArticleDetailDto;
import com.solutionchallenge.factchecker.api.Interests.dto.response.InterestArticleResponseDto;
import com.solutionchallenge.factchecker.api.Interests.dto.response.SelectedInterestsResponseDto;
import com.solutionchallenge.factchecker.api.Interests.service.InterestService;
import com.solutionchallenge.factchecker.api.Member.entity.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name= "Interest Controller ", description = "관심기사 API")
@RestController
@RequestMapping("/api/interests")
@RequiredArgsConstructor
public class InterestController {
    private final InterestService interestService;

    @GetMapping("/selected")
    public ResponseEntity<List<String>> getSelectedInterests(
            @RequestHeader(name = "ACCESS_TOKEN", required = false) String ACCESS_TOKEN,
            @RequestHeader(name = "REFRESH_TOKEN", required = false) String REFRESH_TOKEN) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetailsImpl userDetails = (UserDetailsImpl) principal;

        List<String> selectedInterests = interestService.getSelectedInterests(userDetails.getUsername());
        return ResponseEntity.ok(selectedInterests);
    }

    @PostMapping("/selected")
    public ResponseEntity<SelectedInterestsResponseDto> setSelectedInterests(
            @RequestHeader(name = "ACCESS_TOKEN", required = false) String ACCESS_TOKEN,
            @RequestHeader(name = "REFRESH_TOKEN", required = false) String REFRESH_TOKEN,
            @RequestBody SelectedInterestsRequestDto requestDto) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetailsImpl userDetails = (UserDetailsImpl) principal;

        SelectedInterestsResponseDto responseDto = interestService.setSelectedInterests(userDetails.getUsername(), requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
    @Operation(summary = "홈(메인) 화면 - 관심 기사 전체 목록 가져오기 ", description = "DB에서 관심기사 목록을 받아오기",
            responses = {@ApiResponse(responseCode = "200", description = "관심기사 목록을 성공적으로 조회하였습니다.",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = InterestArticleResponseDto.class))))
            })
    @GetMapping("/getarticle")
    public ResponseEntity<List<InterestArticleResponseDto>> getAllInterestArticles(
            @RequestHeader(name = "ACCESS_TOKEN", required = false) String ACCESS_TOKEN,
            @RequestHeader(name = "REFRESH_TOKEN", required = false) String REFRESH_TOKEN) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetailsImpl userDetails = (UserDetailsImpl) principal;

        List<InterestArticleResponseDto> dtos = interestService.getArticles();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/getarticleBySection")
    public ResponseEntity<List<InterestArticleResponseDto>> getInterestArticlesBySection(
            @RequestParam(name = "section") int section,
            @RequestHeader(name = "ACCESS_TOKEN", required = false) String ACCESS_TOKEN,
            @RequestHeader(name = "REFRESH_TOKEN", required = false) String REFRESH_TOKEN) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetailsImpl userDetails = (UserDetailsImpl) principal;

        List<InterestArticleResponseDto> dtos = interestService.getArticlesBySection(section);
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/getarticle/{id}")
    public ResponseEntity<?> getArticleDetail(
            @PathVariable("id") Long articleId,
            @RequestHeader(name = "ACCESS_TOKEN", required = false) String ACCESS_TOKEN,
            @RequestHeader(name = "REFRESH_TOKEN", required = false) String REFRESH_TOKEN
    ) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetailsImpl userDetails = (UserDetailsImpl) principal;

        InterestArticleDetailDto interestarticleDetail = interestService.getInterestArticleDetailDto(articleId);
        return ResponseEntity.ok().body(interestarticleDetail);
    }



}
