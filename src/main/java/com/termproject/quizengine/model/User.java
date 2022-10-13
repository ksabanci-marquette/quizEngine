package com.termproject.quizengine.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
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

    @NotBlank
    @Size(max = 255)
    private String name;

    @NotBlank
    @Size(max = 255)
    private String lastname;

    @NotBlank
    @Size(max = 255)
    private String username;

    @JsonIgnore
    @NotBlank
    @Size(max = 255)
    private String password;

    @NotBlank
    @Size(max = 50)
    @Email
    @Column(name = "email_address")
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

}
