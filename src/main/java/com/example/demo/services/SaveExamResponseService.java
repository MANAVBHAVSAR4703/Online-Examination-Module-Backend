package com.example.demo.services;

import com.example.demo.models.SaveExamResponse;
import com.example.demo.repositories.SaveExamResponseRepo;
import com.example.demo.responses.ExamResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SaveExamResponseService {
    @Autowired
    private SaveExamResponseRepo saveExamResponseRepo;

    public void saveResponse(Long examId, String email, int currentQuestionIndex, List<?> selectedAnswers, List<?> programmingAnswers){
        Optional<SaveExamResponse> examResponse=saveExamResponseRepo.findByExamIdAndUserEmail(examId,email);
        if(examResponse.isPresent()){
            examResponse.get().setCurrentQuestionIndex(currentQuestionIndex);
            examResponse.get().setSelectedAnswers(selectedAnswers);
            examResponse.get().setProgrammingAnswers(programmingAnswers);
        }
        else {
            SaveExamResponse saveExamResponse=new SaveExamResponse();
            saveExamResponse.setExamId(examId);
            saveExamResponse.setUserEmail(email);
            saveExamResponse.setCurrentQuestionIndex(currentQuestionIndex);
            saveExamResponse.setSelectedAnswers(selectedAnswers);
            saveExamResponse.setProgrammingAnswers(programmingAnswers);
            saveExamResponseRepo.save(saveExamResponse);
        }
    }
}
