package com.solutionchallenge.factchecker.api.Youtube.entity;

import com.solutionchallenge.factchecker.api.Member.entity.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;

@Builder
@Entity
@Getter
@NoArgsConstructor
@Table(name = "related_news")
public class RelatedNews {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "related_news_id", unique = true, nullable = false)
    private Long id;

    private String title;

    @Column(length = 10000)
    @NotNull
    private String article;

    private String upload_date;

    private float credibility;

    @Column(name = "category")
    @NotNull
    @Enumerated(EnumType.ORDINAL)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "youtube_news_id")
    private Youtube youtube;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
    @Builder
    public RelatedNews(Long id, String title, @NotNull String article,String upload_date, float credibility, Category category, Youtube youtube, Member member) {
        this.id = id;
        this.title = title;
        this.article = article;
        this.upload_date = upload_date;
        this.credibility = credibility;
        this.category = category;
        this.youtube = youtube;
        this.member = member;
    }
}
