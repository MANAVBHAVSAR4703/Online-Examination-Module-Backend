package com.example.demo.services;

import com.example.demo.models.CaptureImages;
import com.example.demo.models.Exam;
import com.example.demo.models.MonitorImage;
import com.example.demo.models.User;
import com.example.demo.repositories.CaptureRepository;
import com.example.demo.repositories.ExamRepository;
import com.example.demo.repositories.MonitorRepository;
import com.example.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CaptureService {

    @Autowired
    private CaptureRepository captureRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExamRepository examRepository;

    public void saveImage(String userEmail, byte[] imageBytes,Long examId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with Email: " + userEmail));
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found with id: " + examId));
        CaptureImages captureImages = new CaptureImages();
        captureImages.setUser(user);
        captureImages.setExam(exam);
        captureImages.setImage(imageBytes);
        captureRepository.save(captureImages);
    }

    public List<CaptureImages> FilterByEmailAndExamId(String email, Long examId){
        return captureRepository.findByUserEmailAndExamId(email,examId);
    }
}
