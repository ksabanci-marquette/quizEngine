package com.termproject.quizengine.payload;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class EmailPayload {
    @NotNull private String email;
}
