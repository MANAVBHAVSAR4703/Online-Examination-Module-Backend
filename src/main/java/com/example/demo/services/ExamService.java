package com.example.demo.services;

import com.example.demo.Dto.ExamCreationDto;
import com.example.demo.models.Exam;
import com.example.demo.models.Question;
import com.example.demo.models.Student;
import com.example.demo.repositories.ExamRepository;
import com.example.demo.repositories.QuestionRepository;
import com.example.demo.repositories.StudentRepository;
import com.example.demo.responses.ExamResponse;
import com.example.demo.responses.OptionResponse;
import com.example.demo.responses.QuestionResponse;
import com.example.demo.responses.StudentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExamService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ExamRepository examRepository;

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

    public List<ExamResponse> getExamsByStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        List<Exam> exams = examRepository.findByEnrolledStudentsContains(student);
        return exams.stream().map(this::getExamResponse).collect(Collectors.toList());
    }

}
