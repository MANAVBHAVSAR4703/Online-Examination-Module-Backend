package com.example.demo.Dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class StudentDto {
    // User attributes
    @NotNull
    private String email;
    @NotNull
    private String fullName;
    @NotNull
    private String password;

    // Student-specific attributes
    @NotNull
    private long enrollNo;
    @NotNull
    private String college;

    // Role can be set automatically to "STUDENT" in the service layer
}
