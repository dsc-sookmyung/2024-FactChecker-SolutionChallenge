package com.solutionchallenge.factchecker.api.Interests.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "interest_article")
public class Interest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "interest_article_id", unique = true, nullable = false)
    private Long id;

    @NotNull
    private String title;

    @Column(length = 10000)
    @NotNull
    private String article;

    private float credibility;

    private String date;

    private int section;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setCredibility(float credibility) {
        this.credibility = credibility;
    }

    public void setSection(int section) {
        this.section = section;
    }

}
