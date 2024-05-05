package com.solutionchallenge.factchecker.api.Interests.dto.response;

import java.util.List;

public class InterestResponseDto {
    private List<String> selectedInterests;

    public InterestResponseDto(List<String> selectedInterests) {
        this.selectedInterests = selectedInterests;
    }

}
