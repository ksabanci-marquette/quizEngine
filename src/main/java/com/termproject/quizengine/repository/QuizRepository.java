package com.termproject.quizengine.repository;

import com.termproject.quizengine.dto.UserQuizResultSelectDTO;
import com.termproject.quizengine.dto.UserQuizStat2DTO;
import com.termproject.quizengine.dto.UserQuizStatDTO;
import com.termproject.quizengine.model.Quiz;
import com.termproject.quizengine.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.Tuple;
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

    @Query("SELECT new com.termproject.quizengine.dto.UserQuizStatDTO(max(uqg.grade),u.username) from UserQuizGrade uqg " +
            "left join User u on uqg.userId=u.id where uqg.quizId = :quizId group by uqg.userId" )
    List<UserQuizStatDTO> findQuizStats(@Param("quizId") Long quizId);

    @Query(value="select a.question_id as questionId," +
            "    (select count(*) from user_quiz_question_useranswer where answer_description = user_answer and quiz_id = :quizId and question_id = a.question_id ) as correct," +
            "    (select count(*) from user_quiz_question_useranswer where (answer_description <> user_answer || user_answer is NULL ) and quiz_id = :quizId and question_id = a.question_id) as wrong" +
            " from user_quiz_question_useranswer a where  quiz_id = :quizId group by question_id",nativeQuery = true )
    List<Tuple> findQuizStats2(@Param("quizId") Long quizId);


    @Query(value="select u1.username,u1.name,u1.lastname,T.grade from user u1 left join\n" +
            "(select u.id,uqg.grade from user u left outer join\n" +
            "(select max(grade) as grade,user_id,quiz_id from user_quiz_grade group by user_id,quiz_id) uqg on u.id=uqg.user_id where uqg.quiz_id = :quizId) T\n" +
            "on u1.id=T.id\n" +
            "where u1.isAdmin=false;",nativeQuery = true )
    List<Tuple> findQuizStats3(@Param("quizId") Long quizId);



}
