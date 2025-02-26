package com.example.moderationplanner.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModerationTrack {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "moderator_id", nullable = false)
    private Moderator moderator;

    private String channel;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
