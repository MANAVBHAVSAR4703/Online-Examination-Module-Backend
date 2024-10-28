package com.example.demo.responses;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse<T> {
    private boolean success;
    private String message;
    private T data;
}
