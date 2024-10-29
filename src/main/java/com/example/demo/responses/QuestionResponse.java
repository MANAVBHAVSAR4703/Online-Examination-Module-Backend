package com.example.demo.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResponse<T> {
    private Long id;
    private String text;
    private String category;
    private List<T> options;
    private int correctOptionIndex;
}
