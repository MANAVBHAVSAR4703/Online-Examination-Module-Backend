package com.example.demo.controllers;

import com.example.demo.models.User;
import com.example.demo.responses.ExamResponse;
import com.example.demo.services.ExamService;
import com.example.demo.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@PreAuthorize("hasRole('STUDENT')")
@RequestMapping("/api/student")
public class StudentController {

    private final UserService userService;
    private final ExamService examService;

    StudentController(UserService userService,ExamService examService){
        this.userService=userService;
        this.examService=examService;
    }

    @GetMapping("/")
    public String studentEndpoint(){
        return "Hello, Student";
    }

    @GetMapping("/getExams")
    public ResponseEntity<List<ExamResponse>> getExams(){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String username= ((UserDetails) authentication.getPrincipal()).getUsername();
        User user = (User) userService.loadUserByUsername(username);
        List<ExamResponse> exams = examService.getExamsByStudent(user.getId());
        return ResponseEntity.ok(exams);
    }
}

