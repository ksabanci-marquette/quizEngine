package com.termproject.quizengine.repository;

import com.termproject.quizengine.dto.UserQuizResultSelectDTO;
import com.termproject.quizengine.model.Quiz;
import com.termproject.quizengine.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long>,
        JpaSpecificationExecutor<Quiz> {


    List<Quiz> findAll();


    @Query(value= "select DISTINCT * from (SELECT q.*,uqr.* FROM quiz.quiz q\n" +
            "left join \n" +
            "(SELECT max(last_attempt) as max ,quiz_id,grade FROM quiz.user_quiz_grade where user_id=:userId group by quiz_id, grade) uqr on\n" +
            "q.id = uqr.quiz_id\n" +
            "where q.validThru > now() and uqr.max is null\n" +
            "union \n" +
            "SELECT q.*,uqr.* FROM quiz.quiz q\n" +
            "left join \n" +
            "(SELECT max(last_attempt) as max ,quiz_id,grade FROM quiz.user_quiz_grade where user_id=:userId group by quiz_id, grade) uqr on\n" +
            "q.id = uqr.quiz_id\n" +
            "where q.validThru > now() and q.maxAttempts = uqr.max and uqr.grade is null\n" +
            "union\n" +
            "SELECT q.*,uqr.* FROM quiz.quiz q\n" +
            "left join \n" +
            "(SELECT max(last_attempt) as max ,quiz_id,grade  FROM quiz.user_quiz_grade where user_id=:userId group by quiz_id,grade order by max desc limit 1) uqr on\n" +
            "q.id = uqr.quiz_id\n" +
            "where q.validThru > now() and q.maxAttempts > uqr.max) t order by t.creationDate desc", nativeQuery = true)
    List<Quiz> findQuizzesForUser(@Param("userId") Long userId);


    @Query("SELECT new com.termproject.quizengine.dto.UserQuizResultSelectDTO (u.username, q.id,q.quizName, uqg.creationDate,uqg.grade,uqg.lastAttempt,q.maxAttempts)  FROM  UserQuizGrade uqg left join Quiz q on q.id=uqg.quizId" +
            " left join User u on uqg.userId = u.id" +
            " where uqg.userId=:userId")
    List<UserQuizResultSelectDTO> findLatestQuizResults(@Param("userId") Long userId);


    @Query("SELECT new com.termproject.quizengine.dto.UserQuizResultSelectDTO (u.username, q.id,q.quizName, uqg.creationDate,uqg.grade,uqg.lastAttempt,q.maxAttempts)  FROM  UserQuizGrade uqg left join Quiz q on q.id=uqg.quizId" +
            " left join User u on uqg.userId = u.id")
    List<UserQuizResultSelectDTO> findAllQuizResults();


}
