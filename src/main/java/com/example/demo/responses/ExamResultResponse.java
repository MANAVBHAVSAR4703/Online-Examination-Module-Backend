package com.example.demo.responses;

import com.example.demo.models.ProgrammingQuestion;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
public class ExamResultResponse {
    private Long examId;
    private LocalDateTime examStartTime;
    private int examDuration;
    private String examName;
    private int totalPassed;
    private List<StudentExamResultDto> studentResults;

    @Data
    public static class StudentExamResultDto {
        private String studentEmail;
        private int correctAnswerTotal;
        private List<ProgrammingResponse> programmingQuestionResponses;
        private boolean isPassed;
    }
    @Data
    public static class ProgrammingResponse{
        private ProgrammingQuestion question;
        private String code;
        private String refCode;
    }
}
