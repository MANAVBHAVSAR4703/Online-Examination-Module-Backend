package com.example.demo.models;

import jakarta.persistence.*;
import lombok.*;

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
    private List<Option> options;

    @Column(nullable = false)
    private int correctOptionIndex;
}
