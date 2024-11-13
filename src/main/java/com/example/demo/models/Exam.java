package com.example.demo.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private  String title;
    private LocalDateTime startTime;
    private int duration;
    private boolean isCompleted;
    private double  passingCriteria;

    @ManyToMany
    @JoinTable(
            name="exam_student",
            joinColumns = @JoinColumn(name="exam_id"),
            inverseJoinColumns = @JoinColumn(name="student_id"))
    private List<Student> enrolledStudents;

    @ManyToMany
    @JoinTable(
            name = "exam_questions",
            joinColumns = @JoinColumn(name = "exam_id"),
            inverseJoinColumns = @JoinColumn(name = "question_id")
    )
    private List<Question> questions;

    @ManyToMany
    @JoinTable(
            name = "exam_programming_questions",
            joinColumns=@JoinColumn(name = "exam_id"),
            inverseJoinColumns = @JoinColumn(name = "programming_question_id")
    )
    private List<ProgrammingQuestion> programmingQuestions;
}
