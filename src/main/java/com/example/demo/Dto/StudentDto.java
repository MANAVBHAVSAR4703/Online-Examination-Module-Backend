package com.example.demo.Dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class StudentDto {
    @NotNull
    private String email;
    @NotNull
    private String fullName;
    @NotNull
    private String password;
    @NotNull
    private long enrollNo;
    @NotNull
    private String college;
}
