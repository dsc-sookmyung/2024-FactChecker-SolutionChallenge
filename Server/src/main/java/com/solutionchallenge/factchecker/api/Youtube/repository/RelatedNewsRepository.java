package com.solutionchallenge.factchecker.api.Youtube.repository;


import com.solutionchallenge.factchecker.api.Youtube.entity.RelatedNews;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RelatedNewsRepository extends JpaRepository<RelatedNews,Long> {

}