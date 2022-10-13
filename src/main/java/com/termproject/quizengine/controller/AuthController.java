package com.termproject.quizengine.controller;

import com.termproject.quizengine.payload.EmailPayload;
import com.termproject.quizengine.model.User;
import com.termproject.quizengine.payload.ChangePasswordRequest;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.util.Optional;

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
    public void changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        try {
            userService.updatePassword(changePasswordRequest);
        } catch (Exception e) {
            throw new RuntimeException("Şifre değiştirme başarısız!");
        }
    }

    @PostMapping(path = "/change-password-for-me")
    public void changePasswordForMe(@RequestBody ChangePasswordRequest changePasswordRequest, @CurrentUser UserPrincipal userPrincipal) {
        if(!passwordEncoder.encode(changePasswordRequest.getCurrentPassword()).equals(userPrincipal.getPassword()))
        {
            throw new RuntimeException("Check your current password and try again!");
        }
        try {
            userService.updatePasswordForMe(userPrincipal.getRecordId(),changePasswordRequest.getPassword());
        } catch (Exception e) {
            throw new RuntimeException("Unable to Update Password!");
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

    @GetMapping("/reset/finish")
    public RedirectView process(@RequestParam(value = "key") String key) {
        Optional<User> user = null;
        String redirectPage = "";

        user = userService.getByPasswordResetKey(key);
        if (!user.isPresent()) {
            redirectPage = environment.getProperty("spring.frontend.ChangePasswordPage")+"/:123";
        } else {
            redirectPage = environment.getProperty("spring.frontend.ChangePasswordPage")+"/:"+key;
        }

        RedirectView rv = new RedirectView();
        rv.setContextRelative(false);
        rv.setUrl(redirectPage);
        return rv;
    }


    @GetMapping("/redirect-to-login")
    public RedirectView redirecttoLogin() {

        return new RedirectView("http://localhost:3000/auth-server/login");

    }
}