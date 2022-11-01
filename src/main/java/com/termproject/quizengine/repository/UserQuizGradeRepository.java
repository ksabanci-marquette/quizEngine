package com.termproject.quizengine.repository;

import com.termproject.quizengine.model.UserQuizGrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserQuizGradeRepository extends JpaRepository<UserQuizGrade, Long>,
        JpaSpecificationExecutor<UserQuizGrade> {


    List<UserQuizGrade> findAll();

    @Query(value="SELECT MAX(current_attempt) FROM user_quiz_question_useranswer WHERE user_id= :userId" +
            "  AND quiz_id = :quizId", nativeQuery = true)
    Optional<Integer> findMaxAttemptbyQuizandUser(@Param("userId") Long userId,
                                                  @Param("quizId") Long quizId);



}
