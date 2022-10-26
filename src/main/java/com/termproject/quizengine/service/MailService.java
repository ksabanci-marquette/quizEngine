package com.termproject.quizengine.service;

import com.termproject.quizengine.model.MailLog;
import com.termproject.quizengine.model.User;
import com.termproject.quizengine.repository.MailLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Service
public class MailService {

    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    MessageSource messageSource;

    @Autowired
    Environment environment;

    @Autowired
    MailLogRepository mailLogRepository;

    @Value("${spring.mail.send}")
    private String sendMail ;

    public void createMailLog(User user, String to, String subject, String content, boolean isMultipart, boolean isHtml, String uuid) {
        try {

            MailLog mailLog = new MailLog();
            mailLog.setUuid(uuid);
            mailLog.setCreatedDate(new Date());
            mailLog.setUserId(user.getId());
            mailLog.setUser(user);
            mailLog.setUsername(user.getUsername());
            mailLog.setTo(to);
            mailLog.setSubject(subject);
            mailLog.setContent(content.substring(0, 250));
            mailLog.setIsSent(true);
            mailLogRepository.save(mailLog);
        } catch (Exception ex) {
            throw new RuntimeException("Mail send unsuccessfull for: " + user.getEmailAddress());
        }
    }

    @Async
    public void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
        log.debug("Send email[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}",
                isMultipart, isHtml, to, subject, content);
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, StandardCharsets.UTF_8.name());
            message.setTo(to);
            message.setFrom(environment.getProperty("spring.mail.username"));
            message.setSubject(subject);
            message.setText(content, isHtml);

            javaMailSender.send(mimeMessage);
            log.debug("Sent email to User '{}'", to);
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.warn("Email could not be sent to user '{}'", to, e);
            } else {
                log.warn("Email could not be sent to user '{}': {}", to, e.getMessage());
            }
        }
    }

    @Async
    public void sendEmailwithAuth(User user, String to, String subject, String content, boolean isMultipart, boolean isHtml, String uuid) {
        log.debug("Send email[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}",
                isMultipart, isHtml, to, subject, content);
        Boolean mailSent =false;
        final String username = environment.getProperty("spring.mail.username");
        final String password = environment.getProperty("spring.mail.password");
        Properties props = new Properties();
        props.put("mail.smtp.auth", environment.getProperty("spring.mail.properties.mail.smtp.auth"));
        props.put("mail.smtp.starttls.enable", environment.getProperty("spring.mail.properties.mail.smtp.starttls.enable"));
        props.put("mail.smtps.ssl.checkserveridentity", environment.getProperty("spring.mail.properties.mail.smtps.ssl.checkserveridentity"));
        props.put("mail.smtps.ssl.trust", environment.getProperty("spring.mail.properties.mail.smtps.ssl.trust"));
        props.put("mail.smtp.host", environment.getProperty("spring.mail.host"));
        props.put("mail.smtp.port", environment.getProperty("spring.mail.port"));
        props.put("mail.smtps.timeout", environment.getProperty("spring.mail.properties.mail.smtps.timeout"));
        props.put("mail.smtp.debug", true);
        try {
            createMailLog(user, to, subject, content, isMultipart, isHtml, uuid);
            Session session = Session.getInstance(props,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, password);
                        }
                    });

            session.setDebug(true);
            if("true".equals(sendMail)) {
                MimeMessage message = new MimeMessage(session);
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
                message.setFrom(new InternetAddress(environment.getProperty("spring.mail.username")));
                message.setSubject(subject);
                message.setText(content, StandardCharsets.UTF_8.name(), "html");
                Transport.send(message);
                mailSent = true;
                log.debug("Sent email to User '{}'", to);
            }
            MailLog mailLogByUuid;
            Optional<MailLog> optionalMailLog = mailLogRepository.findByUuid(uuid);
            if (optionalMailLog.isPresent()){
                mailLogByUuid = optionalMailLog.get();
                mailLogByUuid.setIsSent(mailSent);
                mailLogByUuid.setSendDate(new Date());
                mailLogRepository.saveAndFlush(mailLogByUuid);
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (log.isDebugEnabled()) {
                log.warn("Email could not be sent to user '{}'", to, e);
            } else {
                log.warn("Email could not be sent to user '{}': {}", to, e.getMessage());
            }
            throw new RuntimeException("Cannot send email to " + to);
        }
    }

    @Async
    public String sendPasswordResetMail(User user) {
        log.debug("Sending password reset email to '{}'", user.getEmailAddress());
        try {
            String title = "Quiz Portal Password Reset Email";
            //String url=environment.getProperty("spring.baseUrl")+"/api/auth/reset/finish?key="+user.getResetKey();
            String url = environment.getProperty("spring.frontend.ChangePasswordPage") + "/:" + user.getResetKey();
            StringBuilder sb = new StringBuilder();
            sb.append("<!DOCTYPE html>\n");
            sb.append("<html>\n");
            sb.append("    <head>\n");
            sb.append("        <title th:text= ");
            sb.append(messageSource.getMessage("email.reset.title", null, null));
            sb.append("></title>\n");
            sb.append("        <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n");
            sb.append("    </head>\n");
            sb.append("    <body>\n");
            sb.append("        <p>Dear ");
            sb.append(user.getName());
            sb.append(" ");
            sb.append(user.getLastname());
            sb.append(",</p>\n");
            sb.append("<p>");
            sb.append(messageSource.getMessage("email.reset.text1", null, null));
            sb.append("</p>\n");
            sb.append("<a href=\"");
            sb.append(url);
            sb.append("\">");
            sb.append(url);
            sb.append("</a>\n<p>");
            sb.append(messageSource.getMessage("email.reset.text2", null, null));
            sb.append("</p>\n");
            sb.append("<p>");
            sb.append(messageSource.getMessage("email.signature", null, null));
            sb.append("</p>\n");
            sb.append("</body>\n");
            sb.append("</html>\n");
            sendEmailFromTemplate(user, sb.toString(), title);
            return "OK";
        } catch (Exception ex) {
            ex.printStackTrace();
            return ex.getMessage();
        }
    }


    @Async
    public void sendEmailFromTemplate(User user, String content, String title) {
        try {
            UUID uuid;
            String randomUUIDString;
            uuid = UUID.randomUUID();
            randomUUIDString = uuid.toString();

            if (user.getEmailAddress() != null && user.getEmailAddress() != "") {
                sendEmailwithAuth(user, user.getEmailAddress(), title, content, false, true, randomUUIDString);
            } else
                log.debug("email field is null cannot send!", user);
        }
        catch (Exception ex){

            ex.printStackTrace();
        }

    }
}

