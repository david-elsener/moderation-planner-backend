package com.example.moderationplanner.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Moderator {

    @Id
    @GeneratedValue
    private UUID id;

    private String firstName;
    private String lastName;

    @Lob
    private byte[] imageData;
}
