package com.example.demo.Dto;

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
    private String category; // "Logical", "Technical", or "Programming"

    @NotNull
    private List<String> options; // 2-4 options

    @NotNull
    private Integer correctOptionIndex; // Index of the correct option
}
