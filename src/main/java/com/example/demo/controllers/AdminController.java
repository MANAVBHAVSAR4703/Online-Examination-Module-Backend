package com.example.demo.controllers;

import com.example.demo.Dto.ExamCreationDto;
import com.example.demo.Dto.ExamUpdateDto;
import com.example.demo.Dto.QuestionDto;
import com.example.demo.Dto.StudentDto;
import com.example.demo.models.*;
import com.example.demo.repositories.ExamRepository;
import com.example.demo.repositories.ExamResultRepository;
import com.example.demo.responses.*;
import com.example.demo.services.AdminService;
import com.example.demo.services.ExamService;
import com.example.demo.services.QuestionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    private ExamRepository examRepository;

    @Autowired
    private ExamResultRepository examResultRepository;

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

    @GetMapping("/getOverview")
    public ResponseEntity<RegisterResponse<OverviewResponse>> getOverview(){
        try {
            int LogicalQuestionsCount = questionService.getAllQuestions("Logical").size();
            int TechnicalQuestionsCount = questionService.getAllQuestions("Technical").size();
            int ProgrammingQuestionsCount = questionService.getAllQuestions("Programming").size();
            int ExamCount = examService.getALlExams().size();
            int studentCount = adminService.getStudentsFromDb().size();
            int collegesCount = adminService.getDistinctColleges().size();

            OverviewResponse overviewResponse = new OverviewResponse(
                    LogicalQuestionsCount,
                    TechnicalQuestionsCount,
                    ProgrammingQuestionsCount,
                    ExamCount,
                    studentCount,
                    collegesCount);

            return ResponseEntity.ok(new RegisterResponse<>(
                    true,
                    "Overview Fetched Successfully",
                    overviewResponse));
        }catch (Exception e){
            return ResponseEntity.ok(new RegisterResponse<>(
                    true,
                    "Overview Fetching Failed",
                    null));
        }
    }

    @GetMapping("/completed-exams")
    public ResponseEntity<List<ExamResultResponse>> getCompletedExams() {
        List<Exam> completedExams = examRepository.findByIsCompleted(true);

        List<ExamResultResponse> examResponses = completedExams.stream().map(exam -> {
            List<ExamResult> results = examResultRepository.findByExam(exam);
            int totalPassed = (int) results.stream().filter(ExamResult::isPassed).count();

            List<ExamResultResponse.StudentExamResultDto> studentResults = results.stream()
                    .map(result -> {
                        ExamResultResponse.StudentExamResultDto dto = new ExamResultResponse.StudentExamResultDto();
                        dto.setStudentEmail(result.getStudent().getEmail());
                        dto.setCorrectAnswerTotal(result.getCorrectAnswerTotal());
                        dto.setPassed(result.isPassed());
                        return dto;
                    })
                    .collect(Collectors.toList());

            ExamResultResponse examResponseDto = new ExamResultResponse();
            examResponseDto.setExamId(exam.getId());
            examResponseDto.setExamName(exam.getTitle()); // Assuming you have a name field in your Exam entity
            examResponseDto.setTotalPassed(totalPassed);
            examResponseDto.setStudentResults(studentResults);
            return examResponseDto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(examResponses);
    }

    @PostMapping("/editStudent")
    public ResponseEntity<RegisterResponse<StudentResponse>>  editStudentInfo(@Validated @RequestBody StudentResponse studentResponse){
        try{
            Student student =adminService.editStudent(studentResponse);
            StudentResponse editedStudentResponse = new StudentResponse(
                    student.getUser().getEmail(),
                    student.getUser().getFullName(),
                    student.getEnrollNo(),
                    student.getCollege(),
                    student.getUser().getRole()
            );
            return ResponseEntity.ok(new RegisterResponse<>(
                    true,
                    "Student Updated Succesfully",
                    editedStudentResponse));
        }catch (Exception e){
            return ResponseEntity.ok(new RegisterResponse<>(
                    false,
                    "Student Updation Failed",
                    null));
        }
    }

    @PostMapping("/deleteStudent")
    public ResponseEntity<RegisterResponse<StudentResponse>>  deleteStudent(@Validated @RequestBody StudentResponse studentResponse){
        try{
            Student student =adminService.deleteStudent(studentResponse.getEmail());
            StudentResponse editedStudentResponse = new StudentResponse(
                    student.getUser().getEmail(),
                    student.getUser().getFullName(),
                    student.getEnrollNo(),
                    student.getCollege(),
                    student.getUser().getRole()
            );
            return ResponseEntity.ok(new RegisterResponse<>(
                    true,
                    "Student Deleted Succesfully",
                    editedStudentResponse));
        }catch (Exception e){
            return ResponseEntity.ok(new RegisterResponse<>(
                    false,
                    "Student Deletion Failed: "+e.getMessage(),
                    null));
        }
    }

    @PostMapping("/editQuestion")
    public ResponseEntity<RegisterResponse<QuestionResponse<OptionResponse>>> editQuestion(@Validated @RequestBody QuestionResponse<OptionResponse> questionResponse){
        try{
            Question question =adminService.editQuestion(questionResponse);
            return ResponseEntity.ok(new RegisterResponse<>(
                    true,
                    "Question Updated Succesfully",
                    questionService.getQuestionResponse(question)));
        }catch (Exception e){
            return ResponseEntity.ok(new RegisterResponse<>(
                    false,
                    "Question Updation Failed:" +e.getMessage(),
                    null));
        }
    }

    @PostMapping("/deleteQuestion")
    public ResponseEntity<RegisterResponse<QuestionResponse<OptionResponse>>> deleteStudent(@Validated @RequestBody QuestionResponse<OptionResponse> questionResponse) {
        try {
            Question question = adminService.deleteQuestionById(questionResponse.getId());
            return ResponseEntity.ok(new RegisterResponse<>(
                    true,
                    "Question Deleted Successfully",
                    questionService.getQuestionResponse(question)));
        } catch (DataIntegrityViolationException e) {
            // Check if the error message contains specific constraint information
            if (e.getCause() != null && e.getCause().getMessage().contains("REFERENCE constraint")) {
                return ResponseEntity.ok(new RegisterResponse<>(
                        false,
                        "Question Deletion Failed: Question Already Used in Exam. Try Deleting Exam First.",
                        null));
            }
            return ResponseEntity.ok(new RegisterResponse<>(
                    false,
                    "Question Deletion Failed: Database constraint violation.",
                    null));
        } catch (Exception e) {
            return ResponseEntity.ok(new RegisterResponse<>(
                    false,
                    "Question Deletion Failed: " + e.getMessage(),
                    null));
        }
    }

    @PostMapping("/editExam")
    public ResponseEntity<RegisterResponse<ExamResponse>> editExam(@Validated @RequestBody ExamUpdateDto examUpdateDto){
        try{
            Exam exam=adminService.editExam(examUpdateDto);
            return ResponseEntity.ok(new RegisterResponse<>(
                    true,
                    "Exam Updated Succesfully", examService.getExamResponse(exam)));
        }catch (Exception e){
            return ResponseEntity.ok(new RegisterResponse<>(
                    false,
                    "Exam Updation Failed:" +e.getMessage(),
                    null));
        }
    }

    @PostMapping("/deleteExam/{id}")
    public ResponseEntity<RegisterResponse<?>> deleteExam(@PathVariable Long id){
        try{
            adminService.deleteExamById(id);
            return ResponseEntity.ok(new RegisterResponse<>(
                    true,
                    "Exam Deleted Succesfully",null));
        }catch (Exception e){
            return ResponseEntity.ok(new RegisterResponse<>(
                    false,
                    "Exam Deletion Failed: " +e.getMessage(),
                    null));
        }
    }

}
