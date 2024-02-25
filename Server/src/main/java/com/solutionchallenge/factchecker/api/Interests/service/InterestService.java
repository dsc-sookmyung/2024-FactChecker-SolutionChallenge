package com.solutionchallenge.factchecker.api.Interests.service;
import com.solutionchallenge.factchecker.api.Interests.dto.response.InterestArticleDetailDto;
import com.solutionchallenge.factchecker.api.Interests.exception.ArticleNotFoundException;
import com.solutionchallenge.factchecker.api.Interests.dto.request.SelectedInterestsRequestDto;
import com.solutionchallenge.factchecker.api.Interests.dto.response.InterestArticleResponseDto;
import com.solutionchallenge.factchecker.api.Interests.dto.response.MLResponseDto;
import com.solutionchallenge.factchecker.api.Interests.dto.response.SelectedInterestsResponseDto;
import com.solutionchallenge.factchecker.api.Interests.entity.Interest;
import com.solutionchallenge.factchecker.api.Interests.repository.InterestRepository;
import com.solutionchallenge.factchecker.api.Member.entity.Member;
import com.solutionchallenge.factchecker.api.Member.repository.MemberRepository;

import com.solutionchallenge.factchecker.global.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.slf4j.*;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.util.retry.Retry;

import javax.transaction.Transactional;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class InterestService {
    private final MemberRepository memberRepository;
    private final InterestRepository interestRepository;
    private final WebClient webClient;

    private static final Logger log = LoggerFactory.getLogger(InterestService.class);


    @Autowired
    public InterestService(MemberRepository memberRepository, InterestRepository interestRepository, WebClient.Builder webClientBuilder) {
        this.memberRepository = memberRepository;
        this.interestRepository = interestRepository;

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

    public List<String> getSelectedInterests(String memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with ID: " + memberId));
        List<String> selectedInterests = new ArrayList<>(member.getInterests().values());
        return selectedInterests;
    }

    @Transactional
    public SelectedInterestsResponseDto setSelectedInterests(String memberId, SelectedInterestsRequestDto requestDto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with ID: " + memberId));

        String updatedSelectedInterests = requestDto.getSelectedInterests().get(0);
        member.setSelectedInterests(updatedSelectedInterests);

        return SelectedInterestsResponseDto.builder()
                .memberId(memberId)
                .selectedInterests(updatedSelectedInterests)
                .build();
    }
    public List<InterestArticleResponseDto> getArticles() {
        List<Interest> interests = interestRepository.findAll();
        List<InterestArticleResponseDto> dtos = interests.stream()
                .map(this::convertEntityToDto)
                .collect(Collectors.toList());
        return dtos;
    }

    public void updateArticles(){
        saveInterestsFromMLResponse();
    }

    @Transactional
    public void saveInterestsFromMLResponse() {
        List<MLResponseDto> dtos = fetchDataFromMLServer().block();
        interestRepository.deleteAll();
        for (MLResponseDto dto : dtos) {
            Interest interest = convertDtoToEntity(dto);
            interestRepository.save(interest);
        }
    }

    private Mono<List<MLResponseDto>> fetchDataFromMLServer() {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/interests").build())
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new CustomException("Client error, incorrect request. Details: " + errorBody))))
                .onStatus(HttpStatus::is5xxServerError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new CustomException("Server error, retrying... Details: " + errorBody))))
                .bodyToMono(new ParameterizedTypeReference<List<MLResponseDto>>() {})
                .retryWhen(Retry.max(5)
                        .filter(throwable -> throwable instanceof CustomException && throwable.getMessage().contains("retrying")))
                .onErrorResume(e -> {
                    return Mono.error(new CustomException("ML 서버의 트래픽이 너무 많거나 답을 응답하는데 너무 오래걸립니다. 재요청해주세요. Details: " + e.getMessage()));
                });
    }


    private Interest convertDtoToEntity(MLResponseDto dto) {
        Interest interest = new Interest();
        interest.setTitle(dto.getTitle());
        interest.setArticle(dto.getArticle());
        interest.setDate(dto.getDate());
        interest.setCredibility(dto.getCredibility());
        interest.setSection(dto.getSection());

        return interest;
    }

    private InterestArticleResponseDto convertEntityToDto(Interest interest) {
        InterestArticleResponseDto dto = new InterestArticleResponseDto();
        dto.setId(interest.getId());
        dto.setTitle(interest.getTitle());
        dto.setArticle(interest.getArticle());
        dto.setDate(interest.getDate());
        dto.setCredibility(interest.getCredibility());
        dto.setSection(interest.getSection());
        return dto;
    }


    public List<InterestArticleResponseDto> getArticlesBySection(int section) {
        List<Interest> interests = interestRepository.findBySection(section);
        List<InterestArticleResponseDto> dtos = interests.stream()
                .map(this::convertEntityToDtoBySection)
                .collect(Collectors.toList());

        return dtos;
    }

    public InterestArticleDetailDto getInterestArticleDetailDto(Long articleId) {
        Optional<Interest> interestOptional = interestRepository.findById(articleId);
        if (interestOptional.isEmpty()) {
            throw new ArticleNotFoundException("해당 기사를 찾을 수 없습니다.");
        }
        Interest interest = interestOptional.get();


        return new InterestArticleDetailDto(interest.getTitle(), interest.getArticle());
    }

    private InterestArticleResponseDto convertEntityToDtoBySection(Interest interest) {
        InterestArticleResponseDto dto = new InterestArticleResponseDto();
        dto.setId(interest.getId());
        dto.setTitle(interest.getTitle());
        dto.setArticle(interest.getArticle());
        dto.setDate(interest.getDate());
        dto.setCredibility(interest.getCredibility());
        dto.setSection(99);
        return dto;
    }
}
