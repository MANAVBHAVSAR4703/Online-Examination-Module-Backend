package com.example.demo.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponse {
    private String email;
    private String fullName;
    private long enrollNo;
    private String college;
    private String role;
}
