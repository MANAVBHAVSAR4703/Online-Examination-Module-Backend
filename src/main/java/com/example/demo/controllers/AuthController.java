package com.example.demo.controllers;

import com.example.demo.Dto.LoginDto;
import com.example.demo.Jwt.JwtUtils;
import com.example.demo.models.User;
import com.example.demo.responses.LoginResponse;
import com.example.demo.responses.StudentResponse;
import com.example.demo.responses.UserResponse;
import com.example.demo.services.AuthenticationService;
import com.example.demo.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final AuthenticationService authenticationService;
    private final JwtUtils jwtUtils;
    private final UserService userService;

    @Autowired
    AuthController(AuthenticationService authenticationService, JwtUtils jwtUtils, UserService userService){
        this.authenticationService=authenticationService;
        this.jwtUtils=jwtUtils;
        this.userService=userService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse<?>> login(@RequestBody LoginDto loginDto) {
        try {
            String jwt = authenticationService.authenticate(loginDto);
            System.out.println(jwt);
            String username=jwtUtils.extractUsername(jwt);
            User user=(User) userService.loadUserByUsername(username);
            if(user.getRole().equals("STUDENT")){
                StudentResponse studentResponse = new StudentResponse(
                        user.getEmail(),
                        user.getFullName(),
                        user.getStudent().getEnrollNo(),
                        user.getStudent().getCollege()
                );
                return ResponseEntity.ok(new LoginResponse<>(true, "User logged in successfully",jwt,studentResponse));
            }
            else{
                UserResponse userResponse = new UserResponse(
                        user.getEmail(),
                        user.getFullName()
                );
                return ResponseEntity.ok(new LoginResponse<>(true, "User logged in successfully",jwt,userResponse));
            }
        } catch (IllegalArgumentException e) {
            LoginResponse<Void> response = new LoginResponse<>(
                    false,
                    "User login failed :"+e.getMessage(),
                    null,
                    null
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    // Restrict access to ADMIN role only
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        User newUser = userService.addUser(user);
        return ResponseEntity.ok(newUser); // Return the newly created user
    }

}
