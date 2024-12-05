package com.example.demo.services;

import com.example.demo.models.Exam;
import com.example.demo.models.MonitorImage;
import com.example.demo.models.User;
import com.example.demo.repositories.ExamRepository;
import com.example.demo.repositories.MonitorRepository;
import com.example.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MonitorService {

    @Autowired
    private MonitorRepository monitorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExamRepository examRepository;

    public void saveImage(String userEmail, byte[] imageBytes,Long examId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with Email: " + userEmail));
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found with id: " + examId));
        MonitorImage monitorImage = new MonitorImage();
        monitorImage.setUser(user);
        monitorImage.setExam(exam);
        monitorImage.setImage(imageBytes);
        monitorRepository.save(monitorImage);
    }

    public List<MonitorImage> FilterByEmailAndExamId(String email,Long examId){
        return monitorRepository.findByUserEmailAndExamId(email,examId);
    }
}
