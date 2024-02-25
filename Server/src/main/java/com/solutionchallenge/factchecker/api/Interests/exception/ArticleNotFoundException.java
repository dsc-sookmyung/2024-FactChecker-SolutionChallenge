package com.solutionchallenge.factchecker.api.Interests.exception;

public class ArticleNotFoundException extends RuntimeException {
    public ArticleNotFoundException() {
        super();
    }

    public ArticleNotFoundException(String message) {
        super(message);
    }

    public ArticleNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
