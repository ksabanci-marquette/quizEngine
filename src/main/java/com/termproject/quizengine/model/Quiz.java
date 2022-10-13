package com.termproject.quizengine.model;

import lombok.Data;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name="quiz" , uniqueConstraints = {
        @UniqueConstraint(columnNames = {"quiz_name"}, name="uk_quiz_name")})
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(name="quiz_name", nullable = false)
    private String quizName;

    @NotNull
    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date creationDate;

    @NotNull
    @Column(nullable = false)
    private Long created_by;

    @NotNull
    @Column(nullable = false)
    private int maxAttempts;

    @NotNull
    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date validThru;

    @NotNull
    @Column(nullable = false)
    private int duration;

//    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
//    @JoinColumn(name = "quiz_id")
//    private List<QuizQuestion> quizQuestionList = new ArrayList<>();
}
