package com.solutionchallenge.factchecker.api.Youtube.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solutionchallenge.factchecker.api.Member.entity.Member;
import com.solutionchallenge.factchecker.api.Member.repository.MemberRepository;
import com.solutionchallenge.factchecker.api.Youtube.dto.response.*;
import com.solutionchallenge.factchecker.api.Youtube.entity.*;
import com.solutionchallenge.factchecker.api.Youtube.repository.*;
import com.solutionchallenge.factchecker.global.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.util.retry.Retry;

import java.sql.Timestamp;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class YoutubeService {
    private final YoutubeRepository youtubeRepository;
    private final MemberRepository memberRepository;
    private final RelatedNewsRepository relatedNewsRepository;
    private final WebClient webClient;
    @Autowired
    public YoutubeService(YoutubeRepository youtubeRepository, MemberRepository memberRepository, RelatedNewsRepository relatedNewsRepository, WebClient.Builder webClientBuilder) {
        this.youtubeRepository = youtubeRepository;
        this.memberRepository = memberRepository;
        this.relatedNewsRepository = relatedNewsRepository;

        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024)) // 16MB로 설정
                .build();

        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofMinutes(5));

        this.webClient = webClientBuilder.baseUrl("http://34.22.87.117:5000")
                .exchangeStrategies(strategies)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    public YoutubeSuccessDto processYoutubeNews(String member_id, String url) {
        Optional<Youtube> existingYoutube = youtubeRepository.findByUrlAndMember_Id(url, member_id);
        if (existingYoutube.isPresent()) {
            throw new CustomException("url already processed.");
        }
        sendUrlToMlServer(member_id, url).block();
        return new YoutubeSuccessDto(member_id, url);
    }

    private Mono<MLResponseDto> sendUrlToMlServer(String member_id, String url) {
        return webClient.post()
                .uri("/youtubeNews/related")
                .bodyValue(Collections.singletonMap("url", url))
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(String.class)
                                .flatMap(body -> {
                                    try {
                                        return handleMlServerResponse(member_id, url, body);
                                    } catch (JsonProcessingException e) {
                                        return Mono.error(new RuntimeException("Error processing response: " + e.getMessage(), e));
                                    }
                                });
                    } else if (response.statusCode().is4xxClientError()) {
                        return response.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new CustomException("Client error, incorrect request. Details: " + errorBody)));
                    } else if (response.statusCode().is5xxServerError()) {
                        return response.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new CustomException("Server error, retrying... Details: " + errorBody)));
                    } else {
                        return Mono.error(new CustomException("Unexpected response status: " + response.statusCode()));
                    }
                })
                .retryWhen(Retry.max(5)
                        .filter(throwable -> throwable instanceof CustomException && throwable.getMessage().contains("retrying")))
                .onErrorResume(e -> {
                    log.error("After retries or timeout, processing failed: {}", e.getMessage());
                    return Mono.error(new CustomException("ML 서버의 트래픽이 너무 많거나 처리할 수 없는 URL입니다. Details: " + e.getMessage()));
                });
    }


    private Mono<MLResponseDto> handleMlServerResponse(String member_id, String url, String response) throws JsonProcessingException {
        return Mono.fromCallable(() -> {
            Member member = memberRepository.findMemberById(member_id).orElseThrow(() -> new CustomException("User not found"));
            Timestamp now = new Timestamp(System.currentTimeMillis());

            Youtube youtube = Youtube.builder()
                    .createdDate(now)
                    .modifiedDate(now)
                    .member(member)
                    .url(url)
                    .build();

            Youtube savedyoutube = youtubeRepository.save(youtube);

            ObjectMapper objectMapper = new ObjectMapper();
            MLResponseDto mlResponse = objectMapper.readValue(response, MLResponseDto.class);

            List<RelatedNewsDto> currYoutubeNews = mlResponse.getCurrYoutubeNews();
            List<RelatedNewsDto> relYoutubeNews = mlResponse.getRelYoutubeNews();

            String title = mlResponse.getYt_title();
            String keyword = mlResponse.getKeyword();
            String upload_date = mlResponse.getUploadDate();

            savedyoutube.updateKeywordAndUploadDate(title, keyword, upload_date);
            Youtube updatedyoutube = youtubeRepository.save(savedyoutube);
            currYoutubeNews.forEach(newsDto -> saveRelatedNews(newsDto, Category.LATEST, updatedyoutube));
            relYoutubeNews.forEach(newsDto -> saveRelatedNews(newsDto, Category.RELATED, updatedyoutube));

            return mlResponse;
        });
    }


    public void saveRelatedNews(RelatedNewsDto newsDto, Category category, Youtube youtube) {
        Member member = youtube.getMember();

        RelatedNews relatedNews = RelatedNews.builder()
                .title(newsDto.getTitle())
                .article(newsDto.getArticle())
                .upload_date(newsDto.getDate())
                .credibility(newsDto.getCredibility())
                .category(category)
                .youtube(youtube)
                .member(member)
                .build();

        relatedNewsRepository.save(relatedNews);
    }


    public List<YoutubeResponseDto> fetchYoutubeNews(String member_id) {
        List<Youtube> youtubes = youtubeRepository.findAllByMember_Id(member_id);
        if (youtubes.isEmpty()) {
            throw new CustomException("No Youtube news found for member ID: " + member_id);
        }

        return youtubes.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Transactional
    public YoutubeResponseDto convertToDto(Youtube youtube) {
        List<RelatedNewsDto> curr_youtube_news = youtube.getRelatedNews().stream()
                .filter(news -> news.getCategory() == Category.LATEST)
                .map(news -> new RelatedNewsDto(news.getId(), news.getTitle(), news.getArticle(), news.getUpload_date(), news.getCredibility()))
                .collect(Collectors.toList());

        List<RelatedNewsDto> rel_youtube_news = youtube.getRelatedNews().stream()
                .filter(news -> news.getCategory() == Category.RELATED)
                .map(news -> new RelatedNewsDto(news.getId(), news.getTitle(), news.getArticle(), news.getUpload_date(), news.getCredibility()))
                .collect(Collectors.toList());

        return new YoutubeResponseDto(youtube.getId(), youtube.getYt_title(), youtube.getUrl(),youtube.getKeyword(),youtube.getUpload_date(), curr_youtube_news, rel_youtube_news);
    }



    public ArticleDetailDto getArticleDetail(Long articleId, String memberId) {
        Optional<RelatedNews> relatedNewsOptional = relatedNewsRepository.findById(articleId);
        if (relatedNewsOptional.isEmpty()) {
            throw new CustomException("Related news not found with ID: " + articleId);
        }

        RelatedNews relatedNews = relatedNewsOptional.get();
        if (!relatedNews.getYoutube().getMember().getId().equals(memberId)) {
            throw new CustomException("You are not authorized to access this article.");
        }

        return new ArticleDetailDto(relatedNews.getTitle(), relatedNews.getArticle());
    }




}
