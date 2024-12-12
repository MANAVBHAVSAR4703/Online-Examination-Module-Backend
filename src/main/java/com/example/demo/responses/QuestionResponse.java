package com.example.demo.responses;

import com.example.demo.models.ProgrammingQuestion;
import com.example.demo.models.Question;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
    private Question.Category category;
    private Question.Difficulty difficulty;
    private List<T> options;
    private int correctOptionIndex;
    private byte[] imageData;
    private String imageName;
    private String imageType;
}
