package com.example.demo.repositories;

import com.example.demo.models.SaveExamResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SaveExamResponseRepo extends JpaRepository<SaveExamResponse, Long> {
    Optional<SaveExamResponse> findByExamIdAndUserEmail(Long examId, String userEmail);
}
