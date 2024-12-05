package com.example.demo.models;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id;

    @Column(nullable = false)
    private String text;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    @OneToMany(mappedBy = "question",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Option> options=new ArrayList<>();

    @Column(nullable = false)
    private int correctOptionIndex;

    @Lob
    private byte[] imageData;

    private String imageName;

    private String imageType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Question.Difficulty difficulty;

    public void setOptions(List<Option> options) {
        this.options.addAll(options);
    }

    public enum Difficulty {
        EASY,
        MEDIUM,
        HARD
    }
}
