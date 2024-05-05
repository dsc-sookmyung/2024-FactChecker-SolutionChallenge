package com.solutionchallenge.factchecker.api.Member.repository;

import com.solutionchallenge.factchecker.api.Member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {
    public boolean existsMemberById(String id);
    public boolean existsMemberByNickname(String nickname);
    public Optional<Member> findMemberById(String id);

    @Modifying
    @Query("UPDATE Member m SET m.interests = :interests WHERE m.id = :memberId")
    void saveSelectedInterestForUserId(@Param("memberId") String memberId, @Param("interests") Map<String, String> interests);
}
