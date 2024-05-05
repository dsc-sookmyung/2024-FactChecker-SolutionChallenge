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

    // 회원가입 시 선택한 키워드를 가져오는 메서드
    public List<String> getSelectedInterests(String memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with ID: " + memberId));
        // 사용자의 선택한 키워드가 저장된 Map에서 값들만 추출하여 List로 변환
        List<String> selectedInterests = new ArrayList<>(member.getInterests().values());
        return selectedInterests;
    }

    // 클라이언트가 선택한 키워드를 저장하는 메서드
    @Transactional
    public SelectedInterestsResponseDto setSelectedInterests(String memberId, SelectedInterestsRequestDto requestDto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with ID: " + memberId));

        // 새로운 선택된 관심 분야를 설정
        String updatedSelectedInterests = requestDto.getSelectedInterests().get(0);
        member.setSelectedInterests(updatedSelectedInterests);

        // 업데이트된 선택된 관심 분야를 응답 DTO에 담아서 반환
        return SelectedInterestsResponseDto.builder()
                .memberId(memberId)
                .selectedInterests(updatedSelectedInterests)
                .build();
    }
    public List<InterestArticleResponseDto> getArticles() {
        // DB에서 모든 Interest 엔티티를 조회
        List<Interest> interests = interestRepository.findAll();

        // 조회된 Interest 엔티티 리스트를 InterestArticleResponseDto 리스트로 변환
        List<InterestArticleResponseDto> dtos = interests.stream()
                .map(this::convertEntityToDto)
                .collect(Collectors.toList());
        //FE로 반환
        return dtos;
    }

    public void updateArticles(){
        saveInterestsFromMLResponse();
    }

    @Transactional
    public void saveInterestsFromMLResponse() {
        List<MLResponseDto> dtos = fetchDataFromMLServer().block(); // 리액티브 타입을 동기적으로 처리
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
                // 에러 상태 코드 처리
                .onStatus(HttpStatus::is4xxClientError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new CustomException("Client error, incorrect request. Details: " + errorBody))))
                .onStatus(HttpStatus::is5xxServerError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new CustomException("Server error, retrying... Details: " + errorBody))))
                // 성공 응답 처리
                .bodyToMono(new ParameterizedTypeReference<List<MLResponseDto>>() {})
                .retryWhen(Retry.max(5)
                        .filter(throwable -> throwable instanceof CustomException && throwable.getMessage().contains("retrying")))
                .onErrorResume(e -> {
                    // 여기서는 e.getMessage()를 직접 사용하여 에러 원인을 포함시킵니다.
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
        // Interest 엔티티를 InterestArticleResponseDto로 변환하는 로직
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
        // DB에서 해당 섹션의 Interest 엔티티를 조회
        List<Interest> interests = interestRepository.findBySection(section);

        // 조회된 Interest 엔티티 리스트를 InterestArticleResponseDto 리스트로 변환
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
        // Interest 엔티티를 InterestArticleResponseDto로 변환하는 로직
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
