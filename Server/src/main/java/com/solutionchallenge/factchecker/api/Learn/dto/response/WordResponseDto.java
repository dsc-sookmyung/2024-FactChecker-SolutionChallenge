package com.solutionchallenge.factchecker.api.Learn.dto.response;

import com.solutionchallenge.factchecker.api.Learn.entity.Word;
import lombok.*;
import java.sql.Timestamp;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WordResponseDto {
    private Long id;
    private String word;
    private String mean;
    private Timestamp createdDate;
    private Timestamp modifiedDate;
    private Boolean knowStatus;
    private Boolean showMean;

    public WordResponseDto(Word word) {
        this.id = word.getWordId();
        this.word = word.getWord();
        this.mean = word.getMean();
        this.createdDate = word.getCreatedDate();
        this.modifiedDate = word.getModifiedDate();
        this.knowStatus = word.isKnowStatus();
        this.showMean = false;
    }
}
