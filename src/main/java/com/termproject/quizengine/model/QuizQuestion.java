package com.termproject.quizengine.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Data
@Table(name = "quiz_question")
public class QuizQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "quiz_id")
    private Long quizId;

    @NotNull
    @Column(name = "question_id")
    private Long questionId;

/*
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "question_id")
    private List<Question> questionList = new ArrayList<>();
*/

}
