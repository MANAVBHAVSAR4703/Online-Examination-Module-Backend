package com.example.demo.services;

import com.example.demo.Dto.ExamCreationDto;
import com.example.demo.Dto.ExamResultDto;
import com.example.demo.models.*;
import com.example.demo.repositories.*;
import com.example.demo.responses.ExamResponse;
import com.example.demo.responses.OptionResponse;
import com.example.demo.responses.QuestionResponse;
import com.example.demo.responses.StudentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExamService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExamResultRepository examResultRepository;

    public Exam createExam(ExamCreationDto examDto) {
        // Validate duration
        if (examDto.getDuration() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Duration must be a positive number.");
        }

        Collection<? extends Question> logicalQuestions = questionRepository.findRandomQuestionsByCategory(
                "Logical",
                examDto.getLogicalQuestionsCount());

        Collection<? extends Question> programmingQuestions = questionRepository.findRandomQuestionsByCategory(
                "Programming",
                examDto.getProgrammingQuestionsCount());

        Collection<? extends Question> technicalQuestions = questionRepository.findRandomQuestionsByCategory(
                "Technical",
                examDto.getTechnicalQuestionsCount());

        if (logicalQuestions.size() < examDto.getLogicalQuestionsCount()) {
            throw new IllegalArgumentException(
                    "Not enough logical questions available. Required: " + examDto.getLogicalQuestionsCount() +
                            ", Available: " + logicalQuestions.size());
        }

        if (programmingQuestions.size() < examDto.getProgrammingQuestionsCount()) {
            throw new IllegalArgumentException(
                    "Not enough programming questions available. Required: " + examDto.getProgrammingQuestionsCount() +
                            ", Available: " + programmingQuestions.size());
        }

        if (technicalQuestions.size() < examDto.getTechnicalQuestionsCount()) {
            throw new IllegalArgumentException(
                    "Not enough technical questions available. Required: " + examDto.getTechnicalQuestionsCount() +
                            ", Available: " + technicalQuestions.size());
        }

        // Get the questions and set them in the exam
        Exam exam = new Exam();
        exam.setTitle(examDto.getTitle());
        exam.setStartTime(examDto.getStartTime());
        exam.setDuration(examDto.getDuration());
        exam.setPassingCriteria(examDto.getPassingCriteria());
        exam.setCompleted(false);

        List<Question> questions = new ArrayList<>();
        questions.addAll(logicalQuestions);
        questions.addAll(programmingQuestions);
        questions.addAll(technicalQuestions);
        exam.setQuestions(questions);

        // Validate college and fetch students
        List<Student> students = studentRepository.findByCollege(examDto.getCollege());
        if (students.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "No students found for the specified college: " + examDto.getCollege());
        }
        exam.setEnrolledStudents(students);

        // Save and return the exam
        return examRepository.save(exam);
    }

    public ExamResponse getExamResponse(Exam exam)  {
        // Map the Exam entity to ExamResponse DTO
        ExamResponse examResponse = new ExamResponse();
        examResponse.setId(exam.getId());
        examResponse.setTitle(exam.getTitle());
        examResponse.setStartTime(exam.getStartTime());
        examResponse.setDuration(exam.getDuration());
        examResponse.setPassingCriteria(exam.getPassingCriteria());
        examResponse.setCompleted(exam.isCompleted());

        // Map each Student to StudentResponse
        List<StudentResponse> studentResponses = exam.getEnrolledStudents().stream()
                .map(student -> new StudentResponse(
                        student.getUser().getEmail(),
                        student.getUser().getFullName(),
                        student.getEnrollNo(),
                        student.getCollege(),
                        student.getUser().getRole()))
                .collect(Collectors.toList());
        examResponse.setEnrolledStudents(studentResponses);

        // Map each Question to QuestionResponse
        List<QuestionResponse<OptionResponse>> questionResponses = exam.getQuestions().stream()
                .map(question -> {
                    List<OptionResponse> optionResponses = question.getOptions().stream()
                            .map(option -> {
                                OptionResponse optionResponse = new OptionResponse();
                                optionResponse.setId(option.getId());
                                optionResponse.setText(option.getText());
                                return optionResponse;
                            })
                            .collect(Collectors.toList());

                    return new QuestionResponse<>(
                            question.getId(),
                            question.getText(),
                            question.getCategory(),
                            optionResponses,
                            question.getCorrectOptionIndex()
                    );
                })
                .collect(Collectors.toList());
        examResponse.setQuestions(questionResponses);

        return examResponse;
    }

    public ExamResponse getExamResponseWithoutQuestions(Exam exam)  {
        ExamResponse examResponse = new ExamResponse();
        examResponse.setId(exam.getId());
        examResponse.setTitle(exam.getTitle());
        examResponse.setStartTime(exam.getStartTime());
        examResponse.setDuration(exam.getDuration());
        examResponse.setPassingCriteria(exam.getPassingCriteria());
        examResponse.setCompleted(exam.isCompleted());

        // Map each Student to StudentResponse
        List<StudentResponse> studentResponses = exam.getEnrolledStudents().stream()
                .map(student -> new StudentResponse(
                        student.getUser().getEmail(),
                        student.getUser().getFullName(),
                        student.getEnrollNo(),
                        student.getCollege(),
                        student.getUser().getRole()))
                .collect(Collectors.toList());
        examResponse.setEnrolledStudents(studentResponses);
        examResponse.setQuestions(null);

        return examResponse;
    }

    public List<ExamResponse> getExamsByStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<Exam> exams = examRepository.findByEnrolledStudentsContains(student);

        List<Exam> remainingExams = exams.stream()
                .filter(exam -> !exam.isCompleted() && examResultRepository.findByStudentIdAndExamId(studentId, exam.getId()).isEmpty())
                .toList();

        return remainingExams.stream().map(this::getExamResponseWithoutQuestions).collect(Collectors.toList());
    }

    public Optional<Exam> getExamById(Long id){
        Optional<Exam> exam=examRepository.findById(id);
        return exam;
    }

    public List<Exam> getALlExams(){
        return examRepository.findAll();
    }

    public boolean isExamIdValid(Long id){
        return examRepository.existsById(id);
    }

    public ExamResult submitExam(ExamResultDto examSubmission) {
        Optional<Exam> optionalExam = examRepository.findById(examSubmission.getExamId());
        if (optionalExam.isEmpty()) {
            throw new IllegalArgumentException("Exam not found");
        }
        Exam exam = optionalExam.get();
        Optional<User> studentUserOptional = userRepository.findByEmail(examSubmission.getStudentEmail());
        if (studentUserOptional.isEmpty()) {
            throw new IllegalArgumentException("Student not found");
        }
        User student = studentUserOptional.get();

        int correctAnswers = 0;
        for (ExamResultDto.QuestionResponseDto response : examSubmission.getResponses()) {
            Question question = questionRepository.findById(response.getQuestionId())
                    .orElseThrow(() -> new IllegalArgumentException("Question not found: " + response.getQuestionId()));

            if (question.getCorrectOptionIndex() == response.getSelectedOption()) {
                correctAnswers++;
            }
        }

        boolean isPassed = ((double) correctAnswers / exam.getQuestions().size()) * 100 >= exam.getPassingCriteria();

        ExamResult examResult = new ExamResult();
        examResult.setStudent(student);
        examResult.setExam(exam);
        examResult.setCorrectAnswerTotal(correctAnswers);
        examResult.setPassed(isPassed);

        return examResultRepository.save(examResult);
    }

    @Scheduled(fixedRate = 60000)
    public void updateCompletedExams() {
        List<Exam> exams = examRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        for (Exam exam : exams) {
            LocalDateTime examEndTime = exam.getStartTime().plusMinutes(exam.getDuration());
            if (now.isAfter(examEndTime) && !exam.isCompleted()) {
                exam.setCompleted(true);
                examRepository.save(exam);
            }
        }
    }
}
