package com.termproject.quizengine.payload;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.Date;

@Data
@NoArgsConstructor
public class UserSummary {
    @NotNull
    @Size(max = 20, min=5)
    private String username;

    @NotNull
    @Size(max = 20, min=5)
    private String name;

    @NotNull
    @Size(max = 20, min=5)
    private String surname;

    @NotNull
    @Email
    @Size(max = 20)
    private String email;

    @NotNull
    private Boolean isAdmin;

    @NotNull
    private Date creationDate;
    //private Collection<? extends GrantedAuthority> authorities;

    public UserSummary(Long id, String username, String name) {
        this.username = username;
        this.name = name;
    }

    public UserSummary(String username, String name,
                       String surname, String email, Date creationDate, Boolean isAdmin) {
        this.username = username;
        this.name = name;
        this.surname=surname;
        this.email=email;
        this.creationDate = creationDate;
        this.isAdmin=isAdmin;
        //this.authorities=authorities;
    }
}
