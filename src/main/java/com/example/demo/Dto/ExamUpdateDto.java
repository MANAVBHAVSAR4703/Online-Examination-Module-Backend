package com.example.demo.Dto;

import com.example.demo.responses.OptionResponse;
import com.example.demo.responses.QuestionResponse;
import com.example.demo.responses.StudentResponse;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ExamUpdateDto {
    private Long id;
    private String title;
    private LocalDateTime startTime;
    private int duration;
    private double passingCriteria;
}
