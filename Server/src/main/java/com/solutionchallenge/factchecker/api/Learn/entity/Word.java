package com.solutionchallenge.factchecker.api.Learn.entity;

import com.solutionchallenge.factchecker.api.Member.entity.Member;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "words")
public class Word {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "word_Id", unique = true, nullable = false)
    private Long wordId;

    @Column(name = "word")
    @NotNull
    private String word;

    @Column(name = "mean")
    private String mean;

    @Column(name= "know_status")
    @NotNull
    private boolean knowStatus;

    @Column(name = "created_date")
    @NotNull
    private Timestamp createdDate;

    @Column(name = "modified_date")
    @NotNull
    private Timestamp modifiedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public Word(Long wordId, String word, String mean, boolean knowStatus, Timestamp createdDate, Timestamp modifiedDate , Member member) {
        this.wordId = wordId;
        this.word = word;
        this.mean = mean;
        this.knowStatus = knowStatus;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.member = member;
    }

    public void updateWord(String word, String mean, boolean knowStatus) {
        this.word= word;
        this.mean= mean;
        this.knowStatus = knowStatus;
        this.modifiedDate = new Timestamp(System.currentTimeMillis()); //수정 날짜도 현재 시간으로 갱신
    }
}
