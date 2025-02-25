#!/bin/bash

# Define project root
PROJECT_ROOT="moderation-planner-backend"

# Create project directory
mkdir $PROJECT_ROOT
cd $PROJECT_ROOT

# Initialize Gradle project
gradle init --type java-application

# Create necessary directories
mkdir -p src/main/java/com/example/moderationplanner/config
mkdir -p src/main/java/com/example/moderationplanner/controller
mkdir -p src/main/java/com/example/moderationplanner/entity
mkdir -p src/main/java/com/example/moderationplanner/repository
mkdir -p src/main/java/com/example/moderationplanner/service
mkdir -p src/main/resources/db/changelog

# Create build.gradle
cat <<EOL > build.gradle
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.0.0'
    id 'io.spring.dependency-management' version '1.0.15.RELEASE'
}

group = 'com.example'
version = '1.0.0'
sourceCompatibility = '17'

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.liquibase:liquibase-core'
    runtimeOnly 'org.postgresql:postgresql'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}

tasks.named('test') {
    useJUnitPlatform()
}
EOL

# Create settings.gradle
cat <<EOL > settings.gradle
rootProject.name = 'moderation-planner-backend'
EOL

# Create application.yml
cat <<EOL > src/main/resources/application.yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/moderationdb
    username: postgres
    password: postgres
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: true
  liquibase:
    change-log: classpath:/db/changelog/db.changelog-master.yaml

server:
  port: 8080
EOL

# Create Liquibase changelog in YAML
cat <<EOL > src/main/resources/db/changelog/db.changelog-master.yaml
databaseChangeLog:
  - changeSet:
      id: 1
      author: user
      changes:
        - createTable:
            tableName: moderator
            columns:
              - column:
                  name: id
                  type: UUID
              - column:
                  name: first_name
                  type: VARCHAR(255)
              - column:
                  name: last_name
                  type: VARCHAR(255)
              - column:
                  name: image_data
                  type: BYTEA
        - addPrimaryKey:
            tableName: moderator
            columnNames: id
            constraintName: pk_moderator

  - changeSet:
      id: 2
      author: user
      changes:
        - createTable:
            tableName: moderation_track
            columns:
              - column:
                  name: id
                  type: UUID
              - column:
                  name: moderator_id
                  type: UUID
              - column:
                  name: channel
                  type: VARCHAR(255)
              - column:
                  name: start_time
                  type: TIMESTAMP
              - column:
                  name: end_time
                  type: TIMESTAMP
        - addPrimaryKey:
            tableName: moderation_track
            columnNames: id
            constraintName: pk_moderation_track
        - addForeignKeyConstraint:
            baseTableName: moderation_track
            baseColumnNames: moderator_id
            referencedTableName: moderator
            referencedColumnNames: id
            constraintName: fk_moderation_moderator
EOL

# Create Docker Compose for PostgreSQL
cat <<EOL > docker-compose.yml
version: '3.1'

services:
  postgres:
    image: postgres:13
    container_name: moderationdb
    environment:
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
      POSTGRES_DB: moderationdb
    ports:
      - "5432:5432"
    volumes:
      - pgdata:/var/lib/postgresql/data

volumes:
  pgdata:
EOL

# Create main application class
cat <<EOL > src/main/java/com/example/moderationplanner/ModerationPlannerApplication.java
package com.example.moderationplanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ModerationPlannerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ModerationPlannerApplication.class, args);
    }
}
EOL

# Create CORS configuration
cat <<EOL > src/main/java/com/example/moderationplanner/config/CorsConfig.java
package com.example.moderationplanner.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
EOL

# Create Moderator entity with image stored as BYTEA
cat <<EOL > src/main/java/com/example/moderationplanner/entity/Moderator.java
package com.example.moderationplanner.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
public class Moderator {

    @Id
    @GeneratedValue
    private UUID id;

    private String firstName;
    private String lastName;

    @Lob
    private byte[] imageData;

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public byte[] getImageData() { return imageData; }
    public void setImageData(byte[] imageData) { this.imageData = imageData; }
}
EOL

# Create ModeratorRepository
cat <<EOL > src/main/java/com/example/moderationplanner/repository/ModeratorRepository.java
package com.example.moderationplanner.repository;

import com.example.moderationplanner.entity.Moderator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ModeratorRepository extends JpaRepository<Moderator, UUID> {
}
EOL

# Create ModeratorService to handle image storage
cat <<EOL > src/main/java/com/example/moderationplanner/service/ModeratorService.java
package com.example.moderationplanner.service;

import com.example.moderationplanner.entity.Moderator;
import com.example.moderationplanner.repository.ModeratorRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class ModeratorService {

    private final ModeratorRepository moderatorRepository;

    public ModeratorService(ModeratorRepository moderatorRepository) {
        this.moderatorRepository = moderatorRepository;
    }

    public List<Moderator> getAllModerators() {
        return moderatorRepository.findAll();
    }

    public Moderator createModerator(String firstName, String lastName, MultipartFile file) throws IOException {
        Moderator moderator = new Moderator();
        moderator.setFirstName(firstName);
        moderator.setLastName(lastName);
        moderator.setImageData(file.getBytes());
        return moderatorRepository.save(moderator);
    }

    public void deleteModerator(UUID id) {
        moderatorRepository.deleteById(id);
    }
}
EOL

# Create ModeratorController with image upload and retrieval
cat <<EOL > src/main/java/com/example/moderationplanner/controller/ModeratorController.java
package com.example.moderationplanner.controller;

import com.example.moderationplanner.entity.Moderator;
import com.example.moderationplanner.service.ModeratorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/moderators")
public class ModeratorController {

    private final ModeratorService moderatorService;

    public ModeratorController(ModeratorService moderatorService) {
        this.moderatorService = moderatorService;
    }

    @GetMapping
    public List<ModeratorDTO> getAllModerators() {
        return moderatorService.getAllModerators().stream().map(moderator -> new ModeratorDTO(
                moderator.getId(),
                moderator.getFirstName(),
                moderator.getLastName(),
                "data:image/jpeg;base64," + Base64Utils.encodeToString(moderator.getImageData())
        )).collect(Collectors.toList());
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Moderator> createModerator(
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("image") MultipartFile file) {

        try {
            Moderator createdModerator = moderatorService.createModerator(firstName, lastName, file);
            return new ResponseEntity<>(createdModerator, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public void deleteModerator(@PathVariable UUID id) {
        moderatorService.deleteModerator(id);
    }

    // DTO class for returning Base64 encoded images
    public static class ModeratorDTO {
        public UUID id;
        public String firstName;
        public String lastName;
        public String imageData;

        public ModeratorDTO(UUID id, String firstName, String lastName, String imageData) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
            this.imageData = imageData;
        }
    }
}
EOL

echo "🎉 Spring Boot Backend mit PostgreSQL, Liquibase (YAML) und Bildspeicherung wurde erfolgreich erstellt!"
