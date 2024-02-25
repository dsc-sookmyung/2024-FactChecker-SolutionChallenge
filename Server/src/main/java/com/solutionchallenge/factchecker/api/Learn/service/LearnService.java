package com.solutionchallenge.factchecker.api.Learn.service;

import com.solutionchallenge.factchecker.api.Learn.dto.request.ArticleWordRequestDto;
import com.solutionchallenge.factchecker.api.Learn.dto.request.WordDto;
import com.solutionchallenge.factchecker.api.Learn.dto.response.ArticleWordResponse;
import com.solutionchallenge.factchecker.api.Learn.dto.response.ChallengeQuizResponseDto;
import com.solutionchallenge.factchecker.api.Learn.dto.response.DailyQuizScoreResponseDto;
import com.solutionchallenge.factchecker.api.Learn.dto.response.WordResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


//Component Service
@AllArgsConstructor
@Service
public class LearnService {

    private final WordService wordService;
    private final QuizService quizService;

    public List<WordResponseDto> getWordList(String member_id) {
        return wordService.getWordList(member_id);
    }

    public WordResponseDto updateWord( Long wordId, String member_id) {
        return wordService.updateWordStatus(wordId, member_id);

    }

    public List<WordResponseDto> getUnknownWordList(String member_id) {
        return wordService.getUnknownWordList(member_id);
    }


    public DailyQuizScoreResponseDto updateScore(String member_id, String day, int Score) {
        return quizService.updateScore(member_id, day, Score);
    }
    public ChallengeQuizResponseDto challengeDailyQuiz(String member_id) {
        return quizService.challengeDailyQuiz(member_id);
    }

    public List<ArticleWordResponse>  saveWord(String memberId, ArticleWordRequestDto requestDtos) {
        List<ArticleWordResponse> savedWords = new ArrayList<>();

        for (WordDto requestDto : requestDtos.getWords()) {
            ArticleWordResponse articleWordResponse = new ArticleWordResponse();
            articleWordResponse.setWord(requestDto.getWord());
            articleWordResponse.setMean(requestDto.getMean());

            wordService.saveWord(memberId, requestDto);

            savedWords.add(articleWordResponse);
        }

        return savedWords;
    }
}
