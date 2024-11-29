package com.example.demo.Dto;

import lombok.Data;
import java.util.List;

@Data
public class ExamResultDto {
    private String studentEmail;
    private Long examId;
    private List<QuestionResponseDto> responses;
    private List<ProgrammingQuestionDto> programmingQuestionResponses;

    @Data
    public static class QuestionResponseDto {
        private Long questionId;
        private int selectedOption;
    }

    @Data
    public static class ProgrammingQuestionDto {
        private Long questionId;
        private String refCode;
    }

}
