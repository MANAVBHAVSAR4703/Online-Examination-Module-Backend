package com.example.demo.services;

import com.example.demo.Dto.ExamUpdateDto;
import com.example.demo.Dto.StudentDto;
import com.example.demo.models.*;
import com.example.demo.repositories.*;
import com.example.demo.responses.ExamResponse;
import com.example.demo.responses.OptionResponse;
import com.example.demo.responses.QuestionResponse;
import com.example.demo.responses.StudentResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private OptionRepository optionRepository;

    @Autowired
    private ExamResultRepository examResultRepository;

    @Autowired
    private ExamRepository examRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminService(PasswordEncoder passwordEncoder){
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(StudentDto studentDto) {
        if (userRepository.existsByEmail(studentDto.getEmail())) {
            throw new IllegalArgumentException("Student with email " + studentDto.getEmail() + " already exists.");
        }
        // Map StudentDto data to User entity
        User user = new User();
        user.setEmail(studentDto.getEmail());
        user.setFullName(studentDto.getFullName());
        user.setPassword(passwordEncoder.encode(studentDto.getPassword()));

        // Assign role automatically
        user.setRole("STUDENT");
        System.out.println(user);
        // Create Student entity and set specific properties if the role is STUDENT
        if ("STUDENT".equals(user.getRole())) {
            Student student = new Student();
            student.setEnrollNo(studentDto.getEnrollNo());
            student.setCollege(studentDto.getCollege());
            student.setUser(user);  // Link User to Student
            user.setStudent(student);  // Link Student to User
        }

        // Save the user and the student (if present) due to cascading
        return userRepository.save(user);
    }

    public List<Student> getStudentsFromDb(){
        return studentRepository.findAll();
    }

    public List<String> getDistinctColleges(){
        return studentRepository.findDistinctColleges();
    }

    public Student editStudent(StudentResponse studentResponse) {
        Student student = userRepository.findByEmail(studentResponse.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found")).getStudent();

        if (studentResponse.getFullName() != null) {
            student.getUser().setFullName(studentResponse.getFullName());
        }
        if (studentResponse.getEmail() != null) {
            student.getUser().setEmail(studentResponse.getEmail());
        }
        if (studentResponse.getEnrollNo() != 0) {
            student.setEnrollNo(studentResponse.getEnrollNo());
        }
        if (studentResponse.getCollege() != null) {
            student.setCollege(studentResponse.getCollege());
        }

        return studentRepository.save(student);
    }

    @Transactional
    public Question editQuestion(QuestionResponse<OptionResponse> questionResponse) {
        Question question = questionRepository.findById(questionResponse.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found"));

        if (questionResponse.getText() != null) {
            question.setText(questionResponse.getText());
        }
        if (questionResponse.getCategory() != null) {
            question.setCategory(questionResponse.getCategory());
        }

        question.setCorrectOptionIndex(questionResponse.getCorrectOptionIndex());

        List<Option> updatedOptions = new ArrayList<>();

        for (OptionResponse optionResponse : questionResponse.getOptions()) {
            Option option;
            if (optionResponse.getId() != null) {
                option = optionRepository.findById(optionResponse.getId())
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Option not found"));
                option.setText(optionResponse.getText());
            } else {
                option = new Option();
                option.setText(optionResponse.getText());
                option.setQuestion(question);
            }
            updatedOptions.add(option);
        }

        question.getOptions().clear();
        question.setOptions(updatedOptions);
        return questionRepository.save(question);
    }

    @Transactional
    public Student deleteStudent(String email) {
        Student student = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Student not found")).getStudent();

        List<Exam> enrolledExams = examRepository.findByEnrolledStudentsContains(student);
        for (Exam exam : enrolledExams) {
            exam.getEnrolledStudents().remove(student);
            examRepository.save(exam);
        }

        examResultRepository.deleteByStudentId(student.getUser().getId());
        studentRepository.delete(student);
        userRepository.delete(student.getUser());
    return student;
    }

    @Transactional
    public Question deleteQuestionById(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        questionRepository.delete(question);
        return question;
    }

    @Transactional
    public Exam editExam(ExamUpdateDto examResponse) {
        Exam exam = examRepository.findById(examResponse.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exam not found"));

        if (examResponse.getTitle() != null) {
            exam.setTitle(examResponse.getTitle());
        }
        if (examResponse.getStartTime() != null) {
            exam.setStartTime(examResponse.getStartTime());
        }
        if (examResponse.getStartTime() != null) {
            exam.setStartTime(examResponse.getStartTime());
        }
        if (examResponse.getDuration() != 0) {
            exam.setDuration(examResponse.getDuration());
        }
        if (examResponse.getPassingCriteria() != 0) {
            exam.setPassingCriteria(examResponse.getPassingCriteria());
        }
        return examRepository.save(exam);
    }

    @Transactional
    public void deleteExamById(Long examId){
        Exam exam= examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));
        examRepository.delete(exam);
    }
}
