package com.solutionchallenge.factchecker.api.Learn.repository;

import com.solutionchallenge.factchecker.api.Learn.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WordRepository  extends JpaRepository<Word, Long> {
    Optional<Word> findByWordIdAndMember_Id(Long wordId, String memberId);
    List<Word> findAllByMemberIdOrderByCreatedDateDesc(String memberId);
    List<Word> findByMember_IdAndKnowStatus(String memberId,  boolean knowStatus);


}
