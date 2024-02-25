package com.solutionchallenge.factchecker.api.Learn.service;
import java.sql.Timestamp;

import com.solutionchallenge.factchecker.api.Learn.dto.request.WordDto;
import com.solutionchallenge.factchecker.api.Member.entity.Member;
import com.solutionchallenge.factchecker.api.Member.repository.MemberRepository;
import com.solutionchallenge.factchecker.global.exception.CustomException;
import com.solutionchallenge.factchecker.api.Learn.entity.Word;
import com.solutionchallenge.factchecker.api.Learn.repository.WordRepository;
import com.solutionchallenge.factchecker.api.Learn.dto.response.WordResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class WordService {
    private final WordRepository wordRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public WordService(WordRepository wordRepository, MemberRepository memberRepository) {
        this.wordRepository = wordRepository;
        this.memberRepository = memberRepository;
    }


    public List<WordResponseDto> getWordList(String member_id) {
        List<Word> words = wordRepository.findAllByMemberIdOrderByCreatedDateDesc(member_id);
        if(words.isEmpty()){
            throw new CustomException("Words not exist");
        }

        return words.stream()
                .map(WordResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = false)
    public WordResponseDto updateWordStatus(Long wordId, String member_id) {
        Word word = wordRepository.findByWordIdAndMember_Id(wordId, member_id).orElseThrow(()->new CustomException("User not found or word not exist"));
        word.updateWord(word.getWord(), word.getMean(), !word.isKnowStatus());
        Word updatedWord = wordRepository.save(word);
        return new WordResponseDto(updatedWord);
    }

    public List<WordResponseDto> getUnknownWordList(String member_id) {
        List<Word> words = wordRepository.findByMember_IdAndKnowStatus(member_id, false);
        return words.stream()
                .map(WordResponseDto::new)
                .collect(Collectors.toList());


    }
    @Transactional
    public void saveWord(String memberId, WordDto wordDto) {
        Timestamp now = new Timestamp(System.currentTimeMillis());


        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with ID: " + memberId));

        Word word = Word.builder()
                .word(wordDto.getWord())
                .mean(wordDto.getMean())
                .knowStatus(false)
                .createdDate(now)
                .modifiedDate(now)
                .member(member)
                .build();

        wordRepository.save(word);

    }
}
