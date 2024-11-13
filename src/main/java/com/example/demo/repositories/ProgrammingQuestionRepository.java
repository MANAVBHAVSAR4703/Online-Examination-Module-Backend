package com.example.demo.repositories;

import com.example.demo.models.ProgrammingQuestion;
import com.example.demo.models.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

public interface ProgrammingQuestionRepository extends JpaRepository<ProgrammingQuestion,Long>{
    @Query(value = "SELECT q FROM ProgrammingQuestion q ORDER BY FUNCTION('RAND') LIMIT :count")
    Collection<? extends ProgrammingQuestion> findRandomQuestions(@Param("count") int count);
}

