package com.solutionchallenge.factchecker.api.Interests.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SelectedInterestsRequestDto {
    private List<String> selectedInterests;

}
