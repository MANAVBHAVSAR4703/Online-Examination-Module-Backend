package com.example.demo.responses;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ExamResponse {
    private String title;
    private LocalDateTime startTime;
    private int duration;
    private double passingCriteria;
    private boolean isCompleted;
    private List<StudentResponse> enrolledStudents;
    private List<QuestionResponse<OptionResponse>> questions;
}