package com.termproject.quizengine.payload;

import com.termproject.quizengine.model.Question;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class QuizSubmitPayload {
    @NotNull private List<Integer> userAnswers;
    @NotNull private List<Question> quizQuestionList;
    @NotNull private Long quizId;

}
