package com.example.demo.models;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "login_attempts")
public class LoginAttempts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String ipAddress;

    @Column(nullable = false)
    @Lob
    private String userAgent;

    @Column(nullable = false)
    private boolean success;

    private Date timeStamp=new Date();
}
