package com.termproject.quizengine.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.termproject.quizengine.payload.UserSummary;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;

@Entity
@Data
@Table(name = "user", uniqueConstraints = {
        @UniqueConstraint (columnNames = {"email_address"}, name="uk_email"),
        @UniqueConstraint (columnNames = {"username"}, name="uk_username")})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(max = 255)
    @Column(nullable = false)
    private String name;

    @NotNull
    @Size(max = 255)
    @Column(nullable = false)
    private String lastname;

    @NotNull
    @Size(max = 255)
    @Column(nullable = false)
    private String username;

    @JsonIgnore
    @NotNull
    @Size(max = 255)
    @Column(nullable = false)
    private String password;

    @NotNull
    @Size(max = 50)
    @Email
    @Column(name = "email_address",nullable = false)
    private String emailAddress;

    @Column(name = "reset_date")
    private Instant resetDate = null;

    @JsonIgnore
    @Size(max = 20)
    @Column(name = "reset_key", length = 20)
    private String resetKey;

    @NotNull
    @Column(nullable = false)
    private boolean isAdmin = false;

    @NotNull
    @Column(nullable = false)
    private boolean activated = true;

    @NotNull
    @Temporal(TemporalType.DATE)
    @Column(nullable = false, name = "creation_date")
    private Date creationDate;


    public User userSummarytoUser(UserSummary userSummary){
        this.setEmailAddress(userSummary.getEmail());
        this.setName(userSummary.getName());
        this.setLastname(userSummary.getSurname());
        this.setUsername(userSummary.getUsername());
        return this;
    }
}
