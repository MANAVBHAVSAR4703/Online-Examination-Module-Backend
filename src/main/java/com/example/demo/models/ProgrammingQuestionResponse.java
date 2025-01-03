package com.example.demo.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgrammingQuestionResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long questionId;

    @Lob
    private String code;

    @Lob
    private String refCode;

    @ManyToOne
    @JoinColumn(name = "exam_result_id", referencedColumnName = "id")
    private ExamResult examResult;
}
