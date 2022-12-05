package com.termproject.quizengine.repository;

import com.termproject.quizengine.model.Quiz;
import com.termproject.quizengine.model.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizQuestionRepository extends JpaRepository<QuizQuestion, Long>,
        JpaSpecificationExecutor<QuizQuestion> {

    List<QuizQuestion> findAllByQuizId(Long quizId);

    @Modifying
     int deleteAllByQuestionIdIn(List<Long> questionIds);

}
