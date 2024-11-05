package com.example.demo.repositories;

import com.example.demo.models.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.demo.models.ExamResult;

import java.util.List;

@Repository
public interface ExamResultRepository extends JpaRepository<ExamResult, Long> {
    // You can define custom queries if needed, for example:
    List<ExamResult> findByExamId(Long examId);
    List<ExamResult> findByExam(Exam exam);
    List <ExamResult> findByStudentId(Long studentId);
    List<ExamResult> findByStudentIdAndExamId(Long studentId, Long examId);
}
