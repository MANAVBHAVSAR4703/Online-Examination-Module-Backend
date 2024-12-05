package com.example.demo.services;

import com.example.demo.Dto.ProgrammingQuestionDto;
import com.example.demo.Dto.QuestionDto;
import com.example.demo.models.Option;
import com.example.demo.models.ProgrammingQuestion;
import com.example.demo.models.Question;
import com.example.demo.repositories.ProgrammingQuestionRepository;
import com.example.demo.repositories.QuestionRepository;
import com.example.demo.responses.OptionResponse;
import com.example.demo.responses.QuestionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ProgrammingQuestionRepository programmingQuestionRepository;

    public Question createQuestion(QuestionDto questionDto, MultipartFile imageFile) throws IOException {
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
        question.setDifficulty(questionDto.getDifficulty());
        question.setImageData(imageFile.getBytes());
        question.setImageName(imageFile.getOriginalFilename());
        question.setImageType(imageFile.getContentType());
        return questionRepository.save(question);
    }

    public Question createQuestion(QuestionDto questionDto) {
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
        question.setDifficulty(questionDto.getDifficulty());
        return questionRepository.save(question);
    }

    public ProgrammingQuestion createProgrammingQuestion(ProgrammingQuestionDto programmingQuestionDto){
        ProgrammingQuestion programmingQuestion=new ProgrammingQuestion();
        programmingQuestion.setCode(programmingQuestionDto.getCode());
        programmingQuestion.setText(programmingQuestionDto.getText());
        programmingQuestion.setDifficulty(programmingQuestionDto.getDifficulty());
        return programmingQuestionRepository.save(programmingQuestion);
    }

    public List<Question> getAllQuestions(){
        return questionRepository.findAll();
    }

    public List<ProgrammingQuestion> getAllProgrammingQuestions(){
        return programmingQuestionRepository.findAll();
    }

    public List<Question> getAllQuestions(String category){
        return questionRepository.findByCategory(category);
    }

    public QuestionResponse<OptionResponse> getQuestionResponse(Question question){
        List<OptionResponse> optionResponses = question.getOptions().stream()
                .map(option -> {
                    OptionResponse optionResponse = new OptionResponse();
                    optionResponse.setId(option.getId());
                    optionResponse.setText(option.getText());
                    return optionResponse;
                })
                .toList();
        return new QuestionResponse<OptionResponse>(
                question.getId(),
                question.getText(),
                question.getCategory(),
                question.getDifficulty(),
                optionResponses,
                question.getCorrectOptionIndex(),
                question.getImageData(),
                question.getImageName(),
                question.getImageType()
        );
    }
}
