package com.example.demo.Dto;

import com.example.demo.models.Exam;
import com.example.demo.models.Question;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Data
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExamCreationDto {
    private String title;
    private LocalDateTime startTime;
    private int duration; // in minutes
    private double passingCriteria;
    private int logicalQuestionsCount;
    private int technicalQuestionsCount;
    private int programmingQuestionsCount;
    private int programmingSectionQuestionsCount;
    private Exam.Difficulty difficulty;
    private String college; // college of students to enroll
}