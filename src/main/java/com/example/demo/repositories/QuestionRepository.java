package com.example.demo.repositories;

import com.example.demo.models.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question,Long> {
    List<Question> findByCategory(String category);

    @Query(value = "SELECT q FROM Question q WHERE q.category = :category ORDER BY FUNCTION('RAND') LIMIT :count")
    Collection<? extends Question> findRandomQuestionsByCategory(@Param("category") String category, @Param("count") int count);
}
