package com.example.demo.Dto;

import com.example.demo.models.ProgrammingQuestion;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProgrammingQuestionDto {
    private String text;
    private ProgrammingQuestion.Difficulty difficulty;
    private String code;
}
