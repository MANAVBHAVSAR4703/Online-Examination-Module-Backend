package com.example.demo.controllers;

import com.example.demo.Dto.ExamCreationDto;
import com.example.demo.Dto.QuestionDto;
import com.example.demo.Dto.StudentDto;
import com.example.demo.models.*;
import com.example.demo.responses.*;
import com.example.demo.services.AdminService;
import com.example.demo.services.ExamService;
import com.example.demo.services.QuestionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final QuestionService questionService;
    private final ExamService examService;

    @Autowired
    AdminController(AdminService adminService,QuestionService questionService,ExamService examService){
        this.adminService=adminService;
        this.questionService=questionService;
        this.examService=examService;
    }

    @GetMapping("/hello")
    public String adminEndpoint(){
        return "Hello, Admin";
    }

    @PostMapping("/createStudent")
    public ResponseEntity<?> createStudent(@Valid @RequestBody StudentDto studentDto) {
        try {
            User createdUser = adminService.createUser(studentDto);
            Student student = createdUser.getStudent();
            StudentResponse studentResponse = new StudentResponse(
                    createdUser.getEmail(),
                    student.getUser().getUsername(), // Assuming username is the full name
                    student.getEnrollNo(),
                    student.getCollege()
            );
            return ResponseEntity.ok(new RegisterResponse<>(true, "Student created successfully",studentResponse));
        } catch (Exception e) {
            RegisterResponse<Void> response = new RegisterResponse<>(
                    false,
                    "Student creation failed :"+e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @PostMapping("/createQuestion")
    public ResponseEntity<?> createQuestion(@Validated @RequestBody QuestionDto questionDto){
        try {
            Question createdQuestion = questionService.createQuestion(questionDto);
            List<OptionResponse> optionResponses = createdQuestion.getOptions().stream()
                    .map(option -> {
                        OptionResponse optionResponse = new OptionResponse();
                        optionResponse.setId(option.getId());
                        optionResponse.setText(option.getText());
                        return optionResponse;
                    })
                    .toList();
            QuestionResponse<OptionResponse> questionResponse = new QuestionResponse<OptionResponse>(
                    createdQuestion.getId(),
                    createdQuestion.getText(),
                    createdQuestion.getCategory(),
                    optionResponses,
                    createdQuestion.getCorrectOptionIndex()
            );
            return ResponseEntity.ok(new RegisterResponse<>(
                    true,
                    "Question added successfully",
                    questionResponse));

        } catch (Exception e) {
            RegisterResponse<Void> response = new RegisterResponse<>(
                    false,
                    "Question adding failed :"+e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @PostMapping("/createExam")
    public ResponseEntity<?> createExam(@Validated @RequestBody ExamCreationDto examDto) {
        try{
            Exam createdExam = examService.createExam(examDto);
            return ResponseEntity.ok(new RegisterResponse<>(
                    true,
                    "Question added successfully",
                    examService.getExamResponse(createdExam)));
        }
        catch (IllegalArgumentException e){
            RegisterResponse<Void> response = new RegisterResponse<>(
                    false,
                    "Question adding failed : "+e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        catch (Exception e){
            RegisterResponse<Void> response = new RegisterResponse<>(
                    false,
                    "Question adding failed : "+e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

    }
}
