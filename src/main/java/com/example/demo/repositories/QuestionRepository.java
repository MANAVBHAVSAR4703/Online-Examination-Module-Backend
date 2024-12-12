package com.example.demo.repositories;

import com.example.demo.models.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question,Long> {
    List<Question> findByCategory(Question.Category category);

    @Query(value = "SELECT q FROM Question q WHERE q.category = :category AND q.difficulty= :difficulty ORDER BY FUNCTION('RAND') LIMIT :count")
    Collection<? extends Question> findRandomQuestionsByCategoryAndDifficulty(@Param("category") Question.Category category, @Param("difficulty") Question.Difficulty difficulty,@Param("count") int count);

}
