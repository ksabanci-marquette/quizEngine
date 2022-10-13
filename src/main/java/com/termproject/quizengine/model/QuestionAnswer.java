package com.termproject.quizengine.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Data
@Table(name = "question_answer")
public class QuestionAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "question_id")
    private Long questionId;

    @NotNull
    @Column(nullable = false, name = "answer_description")
    private String answerDescription;

    @NotNull
    @Column(nullable = false, name = "is_answer")
    private boolean isAnswer;

}
