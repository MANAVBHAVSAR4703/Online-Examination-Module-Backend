package com.example.demo.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
@Getter
@Setter
public class LoginAttemptDto {
    private String email;

    private String IpAddress;

    private boolean success;

    private String userAgent;
}
