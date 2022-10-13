package com.termproject.quizengine.repository;

import com.termproject.quizengine.model.Question;
import com.termproject.quizengine.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long>,
        JpaSpecificationExecutor<Quiz> {


    List<Quiz> findAll();

}
