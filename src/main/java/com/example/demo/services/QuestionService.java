package com.example.demo.services;

import com.example.demo.Dto.QuestionDto;
import com.example.demo.models.Option;
import com.example.demo.models.Question;
import com.example.demo.repositories.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    public Question createQuestion(QuestionDto questionDto){
        if(questionDto.getOptions().size()<2 || questionDto.getOptions().size()>4){
            throw new IllegalArgumentException("Each question must have between 2 and 4 options.");
        }
        Question question=new Question();
        question.setCategory(questionDto.getCategory());
        question.setText(questionDto.getText());
        question.setCorrectOptionIndex(questionDto.getCorrectOptionIndex());

        List<Option> options=new ArrayList<>();

        for(String optionText:questionDto.getOptions()){
            Option option=new Option();
            option.setText(optionText);
            option.setQuestion(question);
            options.add(option);
        }
        question.setOptions(options);
        return questionRepository.save(question);
    }
}
