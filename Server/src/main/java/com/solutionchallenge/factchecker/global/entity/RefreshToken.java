package com.solutionchallenge.factchecker.global.entity;


import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Entity
@NoArgsConstructor
public class RefreshToken {

    @Id
    @Column(name = "refresh")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @javax.validation.constraints.NotNull
    private String refreshToken;

    @NotNull
    private String email;

    @Builder
    public RefreshToken(String token, String email) {
        this.refreshToken = token;
        this.email = email;
    }

    public RefreshToken updateToken(String token) {
        this.refreshToken = token;
        return this;
    }
}