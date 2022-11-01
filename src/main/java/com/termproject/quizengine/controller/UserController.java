package com.termproject.quizengine.controller;

import com.termproject.quizengine.exception.ResourceNotFoundException;
import com.termproject.quizengine.model.User;
import com.termproject.quizengine.payload.UserSummary;
import com.termproject.quizengine.repository.UserRepository;
import com.termproject.quizengine.security.CurrentUser;
import com.termproject.quizengine.security.UserPrincipal;
import com.termproject.quizengine.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.net.URISyntaxException;


@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    UserService userService;

    @Autowired
    ModelMapper modelMapper;


    @GetMapping("/me")
    public UserSummary getCurrentUser(@CurrentUser UserPrincipal currentUser) {
        UserSummary userSummary = new UserSummary(
                currentUser.getRecordId(),
                currentUser.getUsername(),
                currentUser.getName(),
                currentUser.getSurname(),
                currentUser.getEmailAddress(),
                currentUser.getCreationDate(),
                currentUser.getIsAdmin());
        return userSummary;
    }

    @GetMapping("/{username}")
    public UserSummary getUserProfile(@PathVariable(value = "username") String username) {
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        UserSummary userSummary = new UserSummary( user.getId(),
                user.getUsername(),
                user.getName(),
                user.getLastname(),
                user.getEmailAddress(),
                user.getCreationDate(),
                user.isAdmin());
        return userSummary;
    }


    @PostMapping("/update")
    public ResponseEntity<?> saveProfile(@NotNull  @RequestBody UserSummary userSummary) throws URISyntaxException {
        log.debug("REST request to save UserSummary: {}", userSummary);
        try {

            User user = userRepository.findByUsername(userSummary.getUsername())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "username", userSummary.getUsername()));

            if (userRepository.getByEmailAndRecordIdNotEquals(userSummary.getEmail(), user.getId()).isPresent()) {
                throw  new RuntimeException(userSummary.getEmail() +" email already taken!");
            }
            if (userRepository.getByUsernameAndRecordIdNotEquals(userSummary.getUsername(), user.getId()).isPresent()) {
                throw  new RuntimeException(userSummary.getUsername() +" username already taken!");
            }

            userRepository.save(user.userSummarytoUser(userSummary));

            return new ResponseEntity (user, HttpStatus.OK);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body("An error occured!");
        }
    }
}
