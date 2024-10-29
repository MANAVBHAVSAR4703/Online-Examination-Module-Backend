package com.example.demo.repositories;

import com.example.demo.models.Exam;
import com.example.demo.models.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam,Long> {
    List<Exam> findByEnrolledStudentsContains(Student student);
}
