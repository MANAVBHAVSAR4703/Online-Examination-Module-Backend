package com.example.demo.responses;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse<T> {
    private boolean success;
    private String message;
    private String token;
    private T data;
}
