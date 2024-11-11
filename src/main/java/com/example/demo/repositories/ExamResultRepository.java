package com.example.demo.repositories;

import com.example.demo.models.Exam;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.demo.models.ExamResult;

import java.util.List;

@Repository
public interface ExamResultRepository extends JpaRepository<ExamResult, Long> {
    List<ExamResult> findByExamId(Long examId);
    List<ExamResult> findByExam(Exam exam);
    List <ExamResult> findByStudentId(Long studentId);
    List<ExamResult> findByStudentIdAndExamId(Long studentId, Long examId);

    @Modifying
    @Transactional
    @Query("DELETE FROM ExamResult er WHERE er.student.id = :userId")
    void deleteByStudentId(@Param("userId") Long userId);

}
