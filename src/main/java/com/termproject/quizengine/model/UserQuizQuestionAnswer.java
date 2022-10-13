package com.termproject.quizengine.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Data
@Table(name = "user_quiz_question_useranswer")
public class UserQuizQuestionAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "user_id")
    private Long userId;

    @NotNull
    @Column(name = "quiz_id")
    private Long quizId;

    @NotNull
    @Column(name = "question_id")
    private Long questionId;

    @NotNull
    @Column(nullable = false, name = "answer_description")
    private String realAnswer;

    @Column(name = "user_answer")
    private String userAnswer;

    @NotNull
    @Column(name = "current_attempt")
    private int currentAttempt;

    @NotNull
    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date creationDate;

}
