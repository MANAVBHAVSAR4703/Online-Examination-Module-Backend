package com.example.demo.controllers;

import com.example.demo.models.Exam;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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

    @GetMapping("/getExamById/{id}")  // Use curly braces instead of colon
    public ResponseEntity<ExamResponse> getExamById(@PathVariable Long id) {
        try{
        boolean isExamPresent=examService.isExamIdValid(id);
        Optional<Exam> optionalExam  = examService.getExamById(id);
        if(isExamPresent && optionalExam .isPresent()){
            Exam exam = optionalExam.get();
            ExamResponse examResponse = examService.getExamResponse(exam);
            return ResponseEntity.ok(examResponse);
        }
        else {
            return ResponseEntity.notFound().build();
        }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

