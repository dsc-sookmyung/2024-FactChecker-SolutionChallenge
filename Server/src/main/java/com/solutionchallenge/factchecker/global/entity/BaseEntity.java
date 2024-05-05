package com.solutionchallenge.factchecker.global.entity;

public class BaseEntity<T> {
    private boolean isSuccess;
    private int code;
    private String message;
    private T data;

    public BaseEntity(boolean isSuccess, int code, String message, T data) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
        this.data = data;
    }

}