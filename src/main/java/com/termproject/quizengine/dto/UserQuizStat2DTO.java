package com.termproject.quizengine.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserQuizStat2DTO {

    private String questionId;
    private String correct;
    private String wrong;
}
