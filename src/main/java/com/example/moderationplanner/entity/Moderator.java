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
