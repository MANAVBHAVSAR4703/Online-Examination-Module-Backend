package com.example.demo.services;

import com.example.demo.models.LoginAttempts;
import com.example.demo.repositories.LoginAttemptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class LoginAttemptService {

    @Autowired
    private LoginAttemptRepository loginAttemptRepository;

    public void saveLoginAttempt(String email, String ipAddress, String userAgent, boolean success){
        LoginAttempts loginAttempt=new LoginAttempts();
        loginAttempt.setEmail(email);
        loginAttempt.setSuccess(success);
        loginAttempt.setIpAddress(ipAddress);
        loginAttempt.setUserAgent(userAgent);

        loginAttemptRepository.save(loginAttempt);
    }
}
