package com.termproject.quizengine.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Data
@Table(name = "user_quiz_grade")
public class UserQuizGrade {
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
    @Column(name = "grade")
    private double grade=0.0D;

    @NotNull
    @Column(name = "last_attempt")
    private int lastAttempt;

    @NotNull
    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date creationDate;
}
