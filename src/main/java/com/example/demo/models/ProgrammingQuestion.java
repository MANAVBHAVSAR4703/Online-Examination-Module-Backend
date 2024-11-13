package com.example.demo.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgrammingQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String text;

    @Lob
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Difficulty  difficulty;

    public enum Difficulty {
        EASY,
        MEDIUM,
        HARD
    }
}
