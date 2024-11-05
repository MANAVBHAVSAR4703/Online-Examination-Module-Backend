package com.example.demo.Dto;

import lombok.Data;
import java.util.List;

@Data
public class ExamResultDto {
    private String studentEmail;
    private Long examId;
    private List<QuestionResponseDto> responses;

    @Data
    public static class QuestionResponseDto {
        private Long questionId;
        private int selectedOption;
    }
}
