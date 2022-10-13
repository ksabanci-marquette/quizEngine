package com.termproject.quizengine;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.TimeZone;

@Slf4j
@SpringBootApplication
public class QuizEngineApplication extends SpringBootServletInitializer {

    public static void main(String[] args) throws UnknownHostException {
        Environment env = SpringApplication.run(applicationClass, args).getEnvironment();
        log.info("Access URLs:\n----------------------------------------------------------\n\t" +
                        "Local: \t\thttp://127.0.0.1:{}\n\t" +
                        "External: \thttp://{}:{}\n----------------------------------------------------------",
                env.getProperty("server.port"),
                InetAddress.getLocalHost().getHostAddress(),
                env.getProperty("server.port"));
    }

    public QuizEngineApplication() {
        super();
        setRegisterErrorPageFilter(false);
    }

    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(applicationClass);
    }

    private static Class<QuizEngineApplication> applicationClass = QuizEngineApplication.class;

    @PostConstruct
    void started() {
        TimeZone.setDefault(TimeZone.getTimeZone("America/Chicago"));
    }
}
