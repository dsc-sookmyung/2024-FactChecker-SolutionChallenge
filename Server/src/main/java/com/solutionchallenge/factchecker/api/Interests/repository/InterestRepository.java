package com.solutionchallenge.factchecker.api.Interests.repository;

import com.solutionchallenge.factchecker.api.Interests.entity.Interest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterestRepository extends JpaRepository<Interest, Long> {
    List<Interest> findBySection(int section);
}