package com.solutionchallenge.factchecker.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class GlobalResponseHeader {
    private int code;
    private String message;
}
