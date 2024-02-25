package com.solutionchallenge.factchecker.api.Youtube.entity;

import com.solutionchallenge.factchecker.api.Member.entity.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "youtube_news")
public class Youtube{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "youtube_news_id", unique = true, nullable = false)
    private Long id;

    private String yt_title;
    @NotNull
    private String url;
    @Column(name = "created_date")
    @NotNull
    private Timestamp createdDate;

    @Column(name = "modified_date")
    @NotNull
    private Timestamp modifiedDate;

    private String keyword;

    private String upload_date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "youtube", fetch = FetchType.LAZY)
    private List<RelatedNews> relatedNews = new ArrayList<>();

    @Builder
    public Youtube(Long id, @NotNull String url, @NotNull Timestamp createdDate, @NotNull Timestamp modifiedDate,Member member) {
        this.id = id;
        this.url = url;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.member = member;
    }
    public void updateKeywordAndUploadDate(String title, String keyword, String upload_date) {
        this.yt_title = title;
        this.keyword = keyword;
        this.upload_date = upload_date;
    }
}
