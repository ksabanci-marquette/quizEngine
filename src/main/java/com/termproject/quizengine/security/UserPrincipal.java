package com.termproject.quizengine.security;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Data
public class UserPrincipal implements UserDetails {
    private Long recordId;
    private String name;
    private String surname;
    private String username;
    private Collection<? extends GrantedAuthority> authorities;
    private String emailAddress;
    @JsonIgnore private String password;


    public UserPrincipal(Long recordId, String name, String surname,String username,
                         String emailAddress, String password,
                         Collection<? extends GrantedAuthority> authorities) {
        this.recordId = recordId;
        this.name = name;
        this.surname = surname;
        this.username = username;
        this.emailAddress = emailAddress;
        this.password = password;
        this.authorities = authorities;

    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
