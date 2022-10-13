package com.termproject.quizengine.repository;

import com.termproject.quizengine.model.Question;
import com.termproject.quizengine.model.QuizQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long>,
        JpaSpecificationExecutor<Question> {


    List<Question> findAll();

    List<Question> findAllByIdIn(List<Long> idList);


}
