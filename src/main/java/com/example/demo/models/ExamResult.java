package com.example.demo.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExamResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id", referencedColumnName = "id")
    private User student    ;

    @ManyToOne
    @JoinColumn(name = "exam_id", referencedColumnName = "id")
    private Exam exam;

    private int correctAnswerTotal;
    private boolean isPassed;
}
