package com.solutionchallenge.factchecker.global.repository;

import com.solutionchallenge.factchecker.global.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByEmail(String email);
}
