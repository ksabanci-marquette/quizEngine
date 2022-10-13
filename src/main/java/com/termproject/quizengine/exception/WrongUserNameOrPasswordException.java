package com.termproject.quizengine.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class WrongUserNameOrPasswordException extends RuntimeException{

    public WrongUserNameOrPasswordException(String userNameorEmail) {
        super(String.format(" Username or Email does not exists!", userNameorEmail));
    }
}
