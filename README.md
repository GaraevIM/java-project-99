[![Java CI](https://github.com/GaraevIM/java-project-99/actions/workflows/gradle.yml/badge.svg)](https://github.com/GaraevIM/java-project-99/actions/workflows/gradle.yml)
[![hexlet-check](https://github.com/GaraevIM/java-project-99/actions/workflows/hexlet-check.yml/badge.svg)](https://github.com/GaraevIM/java-project-99/actions/workflows/hexlet-check.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=GaraevIM_java-project-99&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=GaraevIM_java-project-99)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=GaraevIM_java-project-99&metric=coverage)](https://sonarcloud.io/summary/new_code?id=GaraevIM_java-project-99)

# Task Manager

Менеджер задач, разработанный на Spring Boot.

Приложение позволяет управлять пользователями, задачами, статусами задач и метками. Реализована JWT-аутентификация, REST API, валидация данных и документация Swagger/OpenAPI.

## Features

- JWT Authentication
- CRUD Users
- CRUD Task Statuses
- CRUD Labels
- CRUD Tasks
- Task Assignee Support
- Task Labels Support
- Data Validation
- Swagger/OpenAPI Documentation

## Tech Stack

- Java 21
- Spring Boot 3
- Spring Security
- Spring Data JPA
- PostgreSQL
- H2 Database
- JWT
- JUnit 5
- JaCoCo
- SonarCloud
- GitHub Actions

## Local Run

```bash
cd app
./gradlew bootRun
```

Application:

```text
http://localhost:8080
```

Swagger:

```text
http://localhost:8080/swagger-ui/index.html
```

## Demo

Application:

https://java-project-99-vvbc.onrender.com/

Swagger:

https://java-project-99-vvbc.onrender.com/swagger-ui/index.html

Credentials:

```text
Email: hexlet1@example.com
Password: qwerty
```
