package com.termproject.quizengine.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name="mail_log")
public class MailLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Temporal(TemporalType.DATE)
    @Column(name = "creation_date")
    private Date createdDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "send_date")
    private Date sendDate;

    @Column(name = "to")
    private String to;

    @Column (name = "username")
    private String username;

    @Column(name = "subject")
    private String subject;

    @Column(name = "content")
    private String content;

    @Column(name = "uuid", nullable = false)
    private String uuid;

    @Column(name = "is_sent")
    private Boolean isSent;

    @Column(name = "user_id")
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "user_id",insertable = false, updatable = false)
    private User user;

}
