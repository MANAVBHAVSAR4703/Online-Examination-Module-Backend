package com.example.demo.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SaveExamResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long examId;

    @Column(nullable = false)
    private String userEmail;

    @Column(nullable = false)
    private int currentQuestionIndex;

    @Column(nullable = false)
    private List<?> selectedAnswers;

    @Column(nullable = false)
    private List<?> programmingAnswers;

}
