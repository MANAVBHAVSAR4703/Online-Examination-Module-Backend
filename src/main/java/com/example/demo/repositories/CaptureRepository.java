package com.example.demo.repositories;

import com.example.demo.models.CaptureImages;
import com.example.demo.models.Exam;
import com.example.demo.models.MonitorImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CaptureRepository extends JpaRepository<CaptureImages, Long> {
    List<CaptureImages> findByUserEmailAndExamId(String email,Long id);
}

