package com.termproject.quizengine.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class UserQuizResultSelectDTO {

    private String userName;
    private Long id;
    private String quizName;
    private Date creationDate;
    private double grade;
    private int lastAttempt;
    private int maxAttempts;

}
