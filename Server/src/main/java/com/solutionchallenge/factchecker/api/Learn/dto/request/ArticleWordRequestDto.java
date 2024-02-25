package com.solutionchallenge.factchecker.api.Learn.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ArticleWordRequestDto {
    private List<WordDto> words;
}
