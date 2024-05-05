package com.solutionchallenge.factchecker.global.exception;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CustomException  extends RuntimeException {
    @Builder
    public CustomException(String message) { super(message); }
}
