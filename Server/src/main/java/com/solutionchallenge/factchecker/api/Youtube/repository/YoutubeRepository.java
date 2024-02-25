package com.solutionchallenge.factchecker.api.Youtube.repository;

import com.solutionchallenge.factchecker.api.Member.entity.Member;
import com.solutionchallenge.factchecker.api.Youtube.entity.Youtube;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface YoutubeRepository extends JpaRepository<Youtube,Long> {
    Optional<Youtube> findByUrlAndMember_Id(String url, String member_id);
    List<Youtube> findAllByMember_Id(String member_id);

    void deleteByUrlAndMember(String url, Member member);
    Optional<Youtube> findByIdAndMember(Long youtubeId,Member member);
}
