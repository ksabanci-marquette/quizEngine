package com.termproject.quizengine.controller;

import com.termproject.quizengine.model.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class UserController {

    @GetMapping("/users")
    List<User> all() {
        return new ArrayList<>();
    }
}
