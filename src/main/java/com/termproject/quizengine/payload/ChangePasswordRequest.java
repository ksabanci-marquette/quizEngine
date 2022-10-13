package com.termproject.quizengine.payload;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ChangePasswordRequest {

    @NotNull
    private String key;

    private String currentPassword;

    @NotNull
    private String password;

}
