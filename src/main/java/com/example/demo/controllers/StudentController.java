package com.example.demo.controllers;

import com.example.demo.Dto.ExamResultDto;
import com.example.demo.Dto.MonitorDataDto;
import com.example.demo.models.*;
import com.example.demo.responses.ExamResponse;
import com.example.demo.services.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

@RestController
@PreAuthorize("hasRole('STUDENT')")
@RequestMapping("/api/student")
public class StudentController {

    private final UserService userService;
    private final ExamService examService;
    private final MonitorService monitorService;
    private final CaptureService captureService;
    private final SaveExamResponseService saveExamResponseService;

    StudentController(SaveExamResponseService saveExamResponseService,UserService userService,ExamService examService,MonitorService monitorService,CaptureService captureService){
        this.userService=userService;
        this.examService=examService;
        this.monitorService=monitorService;
        this.captureService=captureService;
        this.saveExamResponseService=saveExamResponseService;
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

    @GetMapping("/getExamById/{id}")
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

    @PostMapping("/submitExam")
    public ResponseEntity<?> submitExam(@Validated @RequestBody ExamResultDto examResultDto){
        try{
            ExamResult examResult=examService.submitExam(examResultDto);
            return ResponseEntity.ok(examResult);
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/monitor")
    public ResponseEntity<?> saveMonitorData(@Validated @RequestBody MonitorDataDto request) {
        try {
            byte[] imageBytes = Base64.getDecoder().decode(request.getImage());
            monitorService.saveImage(request.getUserEmail(), imageBytes,request.getExamId());
            return ResponseEntity.ok("Image saved successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving image."+e.getMessage());
        }
    }

    @PostMapping("/capture")
    public ResponseEntity<?> saveCaptureData(@Validated @RequestBody MonitorDataDto request) {
        try {
            captureService.saveImage(request.getUserEmail(),request.getImage().getBytes(),request.getExamId());
            return ResponseEntity.ok("Image saved successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving image."+e.getMessage());
        }
    }

    @PostMapping("/saveExamResponse")
    public ResponseEntity<?> saveExamResponse(@Validated @RequestBody SaveExamResponse saveExamResponse){
        try{
            saveExamResponseService.saveResponse(
                    saveExamResponse.getExamId(),
                    saveExamResponse.getUserEmail(),
                    saveExamResponse.getCurrentQuestionIndex(),
                    saveExamResponse.getSelectedAnswers(),
                    saveExamResponse.getProgrammingAnswers()
            );
            return ResponseEntity.ok("Exam Response Saved Successfully");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving exam response."+e.getMessage());
        }
    }
}

