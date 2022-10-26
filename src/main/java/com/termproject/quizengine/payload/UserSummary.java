package com.termproject.quizengine.payload;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Data
public class UserSummary {
    private String username;
    private String name;
    private String surname;
    private String email;
    private Boolean isAdmin;
    //private Collection<? extends GrantedAuthority> authorities;

    public UserSummary(Long id, String username, String name) {
        this.username = username;
        this.name = name;
    }

    public UserSummary(Long id, String username, String name,
                       String surname, String email, Boolean isAdmin,
                       Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.name = name;
        this.surname=surname;
        this.email=email;
        this.isAdmin=isAdmin;
        //this.authorities=authorities;
    }
}
