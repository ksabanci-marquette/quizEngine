package com.termproject.quizengine.service;

import com.termproject.quizengine.exception.ResourceNotFoundException;
import com.termproject.quizengine.exception.WrongUserNameOrPasswordException;
import com.termproject.quizengine.model.User;
import com.termproject.quizengine.repository.UserRepository;
import com.termproject.quizengine.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        List<GrantedAuthority> grantedAuths=new ArrayList<>();
        User user = userRepository
                .findByUsernameOrEmailAddress(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new WrongUserNameOrPasswordException(usernameOrEmail));
        UserPrincipal userPrincipal= new UserPrincipal(user.getId(),
                user.getName(),
                user.getLastname(),
                user.getUsername(),
                user.getEmailAddress(),
                user.getPassword(),
                user.isAdmin(),
                Collections.emptyList());

        return userPrincipal;
    }

    @Transactional
    public UserDetails loadUserById(Long Id) {

        List<GrantedAuthority> grantedAuths=new ArrayList<>();

        User user = userRepository
                .findById(Id).orElseThrow(() -> new ResourceNotFoundException("User", "id", Id));
        UserPrincipal userPrincipal= new UserPrincipal(user.getId(),
                user.getName(),
                user.getLastname(),
                user.getUsername(),
                user.getEmailAddress(),
                user.getPassword(),
                user.isAdmin(),
                Collections.emptyList());

        return userPrincipal;
    }
}