package com.termproject.quizengine.dto;

import com.termproject.quizengine.model.Question;
import com.termproject.quizengine.model.QuizQuestion;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class QuizDTO {

    private Long id;
    private String quizName;
    private Date creationDate;
    private Long created_by;
    private int maxAttempts;
    private Date validThru;
    private int duration;
    private List<Question> quizQuestionList = new ArrayList<>();

}
