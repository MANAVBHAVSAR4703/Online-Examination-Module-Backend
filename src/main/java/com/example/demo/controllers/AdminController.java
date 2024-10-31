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
import java.util.stream.Collectors;

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
                    student.getCollege(),
                    student.getUser().getRole()
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
            return ResponseEntity.ok(new RegisterResponse<>(
                    true,
                    "Question added successfully",
                    questionService.getQuestionResponse(createdQuestion)));

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
                    "Exam created successfully",
                    examService.getExamResponse(createdExam)));
        }
        catch (IllegalArgumentException e){
            RegisterResponse<Void> response = new RegisterResponse<>(
                    false,
                    "Exam creation failed : "+e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        catch (Exception e){
            RegisterResponse<Void> response = new RegisterResponse<>(
                    false,
                    "Exam creation failed : "+e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

    }

    @GetMapping("/getStudents")
    public ResponseEntity<RegisterResponse<List<StudentResponse>>> getStudents(){
        try {
            List<Student> students = adminService.getStudentsFromDb();
            List<StudentResponse> studentResponseList = students.stream().map(student -> {
                StudentResponse studentResponse = new StudentResponse();
                studentResponse.setFullName(student.getUser().getFullName());
                studentResponse.setRole(student.getUser().getRole());
                studentResponse.setEmail(student.getUser().getEmail());
                studentResponse.setCollege(student.getCollege());
                studentResponse.setEnrollNo(student.getEnrollNo());
                return studentResponse;
            }).toList();
            return ResponseEntity.ok(new RegisterResponse<>(
                    true,
                    "Students Fetched Successfully",
                    studentResponseList));
        } catch (Exception e) {
            return ResponseEntity.ok(new RegisterResponse<>(
                    false,
                    "Students Fetching Failed",
                    null));
        }

    }

    @GetMapping("/getExams")
    public ResponseEntity<RegisterResponse<List<ExamResponse>>> getExams(){
        try {
            List<Exam> exams = examService.getALlExams();
            List<ExamResponse> examResponseList = exams.stream().map(examService::getExamResponse).toList();
            return ResponseEntity.ok(new RegisterResponse<>(
                    true,
                    "Exams Fetched Successfully",
                    examResponseList));
        }catch (Exception e){
            return ResponseEntity.ok(new RegisterResponse<>(
                    true,
                    "Exams Fetching Failed",
                    null));
        }
    }

    @GetMapping("/getQuestions")
    public ResponseEntity<RegisterResponse<List<QuestionResponse<OptionResponse>>>> getQuestions(){
        try {
            List<Question> questions=questionService.getAllQuestions();
            List<QuestionResponse<OptionResponse>> questionResponseList=questions.stream().map(questionService::getQuestionResponse).toList();
            return ResponseEntity.ok(new RegisterResponse<>(
                    true,
                    "Questions Fetched Successfully",
                    questionResponseList));
        }catch (Exception e){
            return ResponseEntity.ok(new RegisterResponse<>(
                    true,
                    "Questions Fetching Failed",
                    null));
        }
    }

}
