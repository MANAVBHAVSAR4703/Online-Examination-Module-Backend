package com.example.demo.responses;

import lombok.Data;

import java.util.List;

@Data
public class ExamResultResponse {
    private Long examId;
    private String examName; // Assuming you have a name for the exam
    private int totalPassed; // Number of students who passed
    private List<StudentExamResultDto> studentResults; // List of student results

    @Data
    public static class StudentExamResultDto {
        private String studentEmail; // Student's email
        private int correctAnswerTotal; // Total correct answers
        private boolean isPassed; // Pass/Fail status
    }
}
