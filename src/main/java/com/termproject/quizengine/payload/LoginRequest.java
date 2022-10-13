package com.termproject.quizengine.payload;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class LoginRequest {
    @NotNull private String usernameOrEmail;
    @NotNull private String password;
}
