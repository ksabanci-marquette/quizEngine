package com.termproject.quizengine.controller;

import com.termproject.quizengine.payload.ChangePasswordRequest;
import com.termproject.quizengine.payload.EmailPayload;
import com.termproject.quizengine.payload.JwtAuthenticationResponse;
import com.termproject.quizengine.payload.LoginRequest;
import com.termproject.quizengine.repository.UserRepository;
import com.termproject.quizengine.security.CurrentUser;
import com.termproject.quizengine.security.JwtTokenProvider;
import com.termproject.quizengine.security.UserPrincipal;
import com.termproject.quizengine.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    JwtTokenProvider tokenProvider;

//    @Autowired
//    MailService mailService;
//
    @Autowired
    UserService userService;

    @Autowired
    Environment environment;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }


    @PostMapping(path = "/update-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        try {
            userService.updatePassword(changePasswordRequest);
        } catch (Exception e) {
 //           throw new RuntimeException("Unsuccesfull !");
            return ResponseEntity.badRequest().body( "Wrong Key provided");

        }
        return ResponseEntity.ok().body( "Update Password successfull");
    }

    @PostMapping(path = "/change-password-for-me")
    public ResponseEntity<?> changePasswordForMe(@RequestBody ChangePasswordRequest changePasswordRequest, @CurrentUser UserPrincipal userPrincipal) {
        if(!passwordEncoder.encode(changePasswordRequest.getCurrentPassword()).equals(userPrincipal.getPassword()))
        {
            return ResponseEntity.badRequest().body( "Check your current password and try again!");
        }
        try {
            userService.updatePasswordForMe(userPrincipal.getRecordId(),changePasswordRequest.getPassword());
            return ResponseEntity.ok().body( "Success");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body( "Wrong Key provided");
        }
    }


    @PostMapping(path = "/reset-password/init")
    public ResponseEntity<?> requestPasswordReset( @RequestBody EmailPayload dto) {
        try {
            return new ResponseEntity<>(userService.requestPasswordReset(dto), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>( ex.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

}