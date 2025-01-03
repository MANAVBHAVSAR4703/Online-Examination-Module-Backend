package com.example.demo.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
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

    @ElementCollection
    @CollectionTable(name = "selected_answers", joinColumns = @JoinColumn(name = "response_id"))
    @Column(name = "answer")
    private List<Integer> selectedAnswers;

    @ElementCollection
    @CollectionTable(name = "selected_programming_answers", joinColumns = @JoinColumn(name = "response_id"))
    @Column(name = "answer")
    private List<String> programmingAnswers;
}
