package com.termproject.quizengine.model;

import com.termproject.quizengine.enums.HardnessLevel;
import com.termproject.quizengine.enums.QuestionType;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name="question" , uniqueConstraints = {
        @UniqueConstraint (columnNames = {"title"}, name="uk_title")})

public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotNull
    @Column(nullable = false)
    private String title;

    @NotNull
    @Column(nullable = false)
    private String description;

    @NotNull
    @Column(nullable = false)
    private QuestionType questionType = QuestionType.SingleChoice ;

    @NotNull
    @Column(nullable = false)
    private HardnessLevel hardnessLevel = HardnessLevel.MEDIUM;

    @NotNull
    @Temporal(TemporalType.DATE)
    @Column(nullable = false, name = "creation_date")
    private Date creationDate;

    @NotNull
    @Temporal(TemporalType.DATE)
    @Column(nullable = false, name = "update_date")
    private Date updateDate;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "question_id")
    private List<QuestionAnswer> questionAnswers = new ArrayList<>();

}
