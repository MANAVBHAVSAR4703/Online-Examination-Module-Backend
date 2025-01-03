package com.example.demo.services;

import com.example.demo.models.SaveExamResponse;
import com.example.demo.repositories.SaveExamResponseRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SaveExamResponseService {
    @Autowired
    private SaveExamResponseRepo saveExamResponseRepo;

    @Transactional
    public void saveResponse(Long examId, String email, int currentQuestionIndex, List<Integer> selectedAnswers, List<String> programmingAnswers) {
        Optional<SaveExamResponse> examResponse = saveExamResponseRepo.findByExamIdAndUserEmail(examId, email);
        if (examResponse.isPresent()) {
            SaveExamResponse existingResponse = examResponse.get();
            existingResponse.setCurrentQuestionIndex(currentQuestionIndex);
            existingResponse.setSelectedAnswers(selectedAnswers);
            existingResponse.setProgrammingAnswers(programmingAnswers);

            saveExamResponseRepo.save(existingResponse);
        } else {
            SaveExamResponse saveExamResponse = new SaveExamResponse();
            saveExamResponse.setExamId(examId);
            saveExamResponse.setUserEmail(email);
            saveExamResponse.setCurrentQuestionIndex(currentQuestionIndex);
            saveExamResponse.setSelectedAnswers(selectedAnswers);
            saveExamResponse.setProgrammingAnswers(programmingAnswers);

            saveExamResponseRepo.save(saveExamResponse);
        }
    }
}
