package com.solutionchallenge.factchecker.api.Youtube.controller;

import com.solutionchallenge.factchecker.api.Member.entity.UserDetailsImpl;
import com.solutionchallenge.factchecker.api.Youtube.dto.request.YoutubeURLRequestDto;
import com.solutionchallenge.factchecker.api.Youtube.dto.response.ArticleDetailDto;
import com.solutionchallenge.factchecker.api.Youtube.dto.response.YoutubeResponseDto;
import com.solutionchallenge.factchecker.api.Youtube.dto.response.YoutubeSuccessDto;
import com.solutionchallenge.factchecker.api.Youtube.service.YoutubeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name= "Youtube Controller", description = "유튜브영상뉴스 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/YoutubeNews")
@Slf4j
public class YoutubeController {
    private final YoutubeService youtubeService;

    @Operation(summary = "팩트체크 페이지 - 유튜브 url 입력", description = "ml서버에 요청을 보내고 받은 응답을 DB에 저장합니다.",
            responses = {@ApiResponse(responseCode = "200", description = "유튜브주소가 성공적으로 저장되었습니다.",
                    content = @Content(schema = @Schema(implementation = YoutubeSuccessDto.class)))
            })
    @PostMapping("/add")
    public ResponseEntity<?> addYoutubeNewsURl(
            @RequestBody YoutubeURLRequestDto youtubeURLRequestDto,
            @RequestHeader(name = "ACCESS_TOKEN", required = false) String ACCESS_TOKEN,
            @RequestHeader(name = "REFRESH_TOKEN", required = false) String REFRESH_TOKEN
    ) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetailsImpl userDetails = (UserDetailsImpl) principal;

        YoutubeSuccessDto dto = youtubeService.processYoutubeNews(userDetails.getUsername(), youtubeURLRequestDto.getUrl());
        return ResponseEntity.ok().body(dto);
    }
    @Operation(summary = "팩트체크 페이지 - 유튜브-관련기사 조회", description = "유튜브-관련기사 쌍을 가져온다",
            responses = {@ApiResponse(responseCode = "200", description = "유튜브-관련기사 쌍을 성공적으로 조회했습니다.",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = YoutubeResponseDto.class))))
            })

    @GetMapping("/getarticles")
    public ResponseEntity<?> getYoutubeNews(
            @RequestHeader(name = "ACCESS_TOKEN", required = false) String ACCESS_TOKEN,
            @RequestHeader(name = "REFRESH_TOKEN", required = false) String REFRESH_TOKEN
    ) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetailsImpl userDetails = (UserDetailsImpl) principal;

        List<YoutubeResponseDto> Dtos = youtubeService.fetchYoutubeNews(userDetails.getUsername());
        return ResponseEntity.ok().body(Dtos);
    }

    @Operation(summary = "팩트체크 페이지 - 기사 상세 조회", description = "특정 기사의 내용과 제목을 가져옵니다.",
            responses = {@ApiResponse(responseCode = "200", description = "기사 내용과 제목을 성공적으로 조회했습니다.",
                    content = @Content(schema = @Schema(implementation = ArticleDetailDto.class)))
            })


    @GetMapping("/getarticle/{id}")
    public ResponseEntity<?> getArticleDetail(
            @PathVariable("id") Long articleId,
            @RequestHeader(name = "ACCESS_TOKEN", required = false) String ACCESS_TOKEN,
            @RequestHeader(name = "REFRESH_TOKEN", required = false) String REFRESH_TOKEN
    ) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetailsImpl userDetails = (UserDetailsImpl) principal;

        ArticleDetailDto articleDetail = youtubeService.getArticleDetail(articleId, userDetails.getUsername());
        return ResponseEntity.ok().body(articleDetail);
    }

}
