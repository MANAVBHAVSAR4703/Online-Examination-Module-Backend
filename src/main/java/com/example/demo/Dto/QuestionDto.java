package com.example.demo.Dto;

import com.example.demo.models.Question;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuestionDto {
    @NotBlank
    private String text;

    @NotBlank
    private String category;

    @NotNull
    private List<String> options;

    @NotNull
    private Integer correctOptionIndex;

    @NotNull
    private Question.Difficulty difficulty;
}
