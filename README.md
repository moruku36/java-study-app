# Study Progress Tracker

[English](README.md) | [日本語](README.ja.md)

A Java 17 study-tracking web application using Spring Boot, Thymeleaf, and H2, with goals, study records, dashboards, history, and weekly batch reports.

## Run locally

Use Java 17 or later and Maven 3.6 or later, or the checked-in Maven Wrapper.

```bash
./mvnw spring-boot:run
```

The application supports subject-specific learning goals, daily study time and notes, weekly progress charts, history search, and Spring Batch weekly reports. The frontend uses Thymeleaf, Bootstrap, and Chart.js; H2 is the development database. Docker and production-deployment details are retained in the Japanese guide and `DEPLOYMENT.md`.


## Contents

- [DEPLOYMENT.md](DEPLOYMENT.md)
- [src/](src)

## Detailed documentation

The [Japanese guide](README.ja.md) retains the complete original setup instructions, configuration, examples, project status, and limitations. Supporting documents keep their existing language.
