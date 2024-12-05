package com.example.demo.controllers;

import com.example.demo.Dto.*;
import com.example.demo.models.*;
import com.example.demo.repositories.ExamRepository;
import com.example.demo.repositories.ExamResultRepository;
import com.example.demo.repositories.ProgrammingQuestionRepository;
import com.example.demo.responses.*;
import com.example.demo.services.AdminService;
import com.example.demo.services.ExamService;
import com.example.demo.services.MonitorService;
import com.example.demo.services.QuestionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final QuestionService questionService;
    private final ExamService examService;
    private final MonitorService monitorService;

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private ExamResultRepository examResultRepository;

    @Autowired
    private ProgrammingQuestionRepository programmingQuestionRepository;

    @Autowired
    AdminController(AdminService adminService,QuestionService questionService,ExamService examService,MonitorService monitorService){
        this.adminService=adminService;
        this.questionService=questionService;
        this.examService=examService;
        this.monitorService=monitorService;
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
    public ResponseEntity<?> createQuestion(@Validated @RequestPart QuestionDto question,@RequestPart(required = false) MultipartFile imageFile){
        try {
            Question createdQuestion = imageFile==null?questionService.createQuestion(question):questionService.createQuestion(question,imageFile);
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

    @PostMapping("/createProgrammingQuestion")
    public ResponseEntity<RegisterResponse<ProgrammingQuestion>> createProgrammingQuestion(@Validated @RequestBody ProgrammingQuestionDto programmingQuestionDto){
        try {
            ProgrammingQuestion createdProgrammingQuestion = questionService.createProgrammingQuestion(programmingQuestionDto);
            return ResponseEntity.ok(new RegisterResponse<>(
                    true,
                    "Programming question added successfully",
                    createdProgrammingQuestion));

        } catch (Exception e) {
            return ResponseEntity.ok(new RegisterResponse<>(
                    false,
                    "Programming question addition failed: "+e.getMessage(),
                    null));
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

    @GetMapping("/getProgrammingQuestions")
    public ResponseEntity<RegisterResponse<List<ProgrammingQuestion>>> getProgrammingQuestions(){
        try {
            List<ProgrammingQuestion> programmingQuestionList=questionService.getAllProgrammingQuestions();
            return ResponseEntity.ok(new RegisterResponse<>(
                    true,
                    "Programming Questions Fetched Successfully",
                    programmingQuestionList));
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
                        List<ExamResultResponse.ProgrammingResponse> programmingResponsesList=result.getProgrammingQuestionResponses().stream().map(progResult->{
                            ExamResultResponse.ProgrammingResponse programmingResponse=new ExamResultResponse.ProgrammingResponse();
                            programmingResponse.setQuestion(programmingQuestionRepository.findById(progResult.getQuestionId()).
                                    orElseThrow(() -> new RuntimeException("Programming Question not found")));
                            programmingResponse.setCode(progResult.getCode());
                            programmingResponse.setRefCode(progResult.getRefCode());
                            return programmingResponse;
                        }).collect(Collectors.toList());
                        dto.setProgrammingQuestionResponses(programmingResponsesList);
                        dto.setCorrectAnswerTotal(result.getCorrectAnswerTotal());
                        dto.setPassed(result.isPassed());
                        return dto;
                    })
                    .collect(Collectors.toList());

            ExamResultResponse examResponseDto = new ExamResultResponse();
            examResponseDto.setExamId(exam.getId());
            examResponseDto.setExamStartTime(exam.getStartTime());
            examResponseDto.setExamDuration(exam.getDuration());
            examResponseDto.setExamName(exam.getTitle());
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
    public ResponseEntity<RegisterResponse<QuestionResponse<OptionResponse>>> editQuestion(@Validated @RequestPart QuestionResponse<OptionResponse> question,
                                                                                           @RequestPart(required = false) MultipartFile imageFile){
        try{
            Question question1 =imageFile==null?adminService.editQuestion(question):adminService.editQuestion(question,imageFile);
            return ResponseEntity.ok(new RegisterResponse<>(
                    true,
                    "Question Updated Succesfully",
                    questionService.getQuestionResponse(question1)));
        }catch (Exception e){
            return ResponseEntity.ok(new RegisterResponse<>(
                    false,
                    "Question Updation Failed:" +e.getMessage(),
                    null));
        }
    }
    @PostMapping("/deleteQuestion")
    public ResponseEntity<RegisterResponse<QuestionResponse<OptionResponse>>> deleteQuestion(@Validated @RequestBody QuestionResponse<OptionResponse> questionResponse) {
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

    @PostMapping("/editProgrammingQuestion")
    public ResponseEntity<RegisterResponse<ProgrammingQuestion>> editProgrammingQuestion(@Validated @RequestBody ProgrammingQuestion programmingQuestion){
        try{
            ProgrammingQuestion editedProgrammingQuestion =adminService.editProgrammingQuestion(programmingQuestion);
            return ResponseEntity.ok(new RegisterResponse<>(
                    true,
                    "Question Updated Succesfully",
                    editedProgrammingQuestion));
        }catch (Exception e){
            return ResponseEntity.ok(new RegisterResponse<>(
                    false,
                    "Programming Question Updation Failed:" +e.getMessage(),
                    null));
        }
    }

    @PostMapping("/deleteProgrammingQuestion/{id}")
    public ResponseEntity<RegisterResponse<ProgrammingQuestion>> deleteProgrammingQuestion(@PathVariable Long id) {
        try {
            ProgrammingQuestion programmingQuestion = adminService.deleteProgrammingQuestionById(id);
            return ResponseEntity.ok(new RegisterResponse<>(
                    true,
                    "Programming Question Deleted Successfully",
                    programmingQuestion));
        } catch (DataIntegrityViolationException e) {
            if (e.getCause() != null && e.getCause().getMessage().contains("REFERENCE constraint")) {
                return ResponseEntity.ok(new RegisterResponse<>(
                        false,
                        "Programming Question Deletion Failed: Question Already Used in Exam. Try Deleting Exam First.",
                        null));
            }
            return ResponseEntity.ok(new RegisterResponse<>(
                    false,
                    "Programming Question Deletion Failed: Database constraint violation.",
                    null));
        } catch (Exception e) {
            return ResponseEntity.ok(new RegisterResponse<>(
                    false,
                    "Programming Question Deletion Failed: " + e.getMessage(),
                    null));
        }
    }

    @GetMapping("/getMonitorImages/{email}/{examId}")
    public ResponseEntity<?> getMonitorData(@PathVariable String  email,@PathVariable Long examId) {
        try {
            List<MonitorImage> monitorImages=monitorService.FilterByEmailAndExamId(email,examId);
            List<MonitorImageResponse> monitorImageResponseList=monitorImages.stream().map(monitorImage -> {
                MonitorImageResponse monitorImageResponse=new MonitorImageResponse();
                monitorImageResponse.setImage(monitorImage.getImage());
                monitorImageResponse.setId(monitorImage.getId());
                monitorImageResponse.setCaptureTime(monitorImage.getCaptureTime());
                return monitorImageResponse;
            }).toList();
            return ResponseEntity.ok(monitorImageResponseList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving image."+e.getMessage());
        }
    }
}
