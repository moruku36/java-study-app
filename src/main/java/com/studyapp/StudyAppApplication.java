package com.studyapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {
    org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class
})
public class StudyAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudyAppApplication.class, args);
    }
} 