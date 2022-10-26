package com.termproject.quizengine.controller;

import com.termproject.quizengine.payload.UserSummary;
import com.termproject.quizengine.repository.UserRepository;
import com.termproject.quizengine.security.CurrentUser;
import com.termproject.quizengine.security.UserPrincipal;
import com.termproject.quizengine.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    UserService userService;



    @GetMapping("/me")
    public UserSummary getCurrentUser(@CurrentUser UserPrincipal currentUser) {
        UserSummary userSummary = new UserSummary(currentUser.getRecordId(),
                currentUser.getUsername(),
                currentUser.getName(),
                currentUser.getSurname(),
                currentUser.getEmailAddress(),
                currentUser.getIsAdmin(),
                currentUser.getAuthorities());
        return userSummary;
    }

}
