package com.termproject.quizengine.payload;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class EmailPayload {
    @NotBlank private String email;
}
