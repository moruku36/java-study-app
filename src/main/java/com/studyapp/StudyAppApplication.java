package com.studyapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;

@SpringBootApplication(exclude = {
    org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
    ManagementWebSecurityAutoConfiguration.class
})
public class StudyAppApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(StudyAppApplication.class);
        // Collect detailed startup steps for /actuator/startup
        app.setApplicationStartup(new BufferingApplicationStartup(2048));
        app.run(args);
    }
} 