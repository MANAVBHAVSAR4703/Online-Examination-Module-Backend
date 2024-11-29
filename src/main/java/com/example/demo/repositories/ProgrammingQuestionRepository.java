package com.example.demo.repositories;

import com.example.demo.models.ProgrammingQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface ProgrammingQuestionRepository extends JpaRepository<ProgrammingQuestion,Long>{
    @Query(value = "SELECT q FROM ProgrammingQuestion q ORDER BY FUNCTION('RAND') LIMIT :count")
    Collection<? extends ProgrammingQuestion> findRandomQuestions(@Param("count") int count);
}

