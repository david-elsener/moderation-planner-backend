package com.example.moderationplanner.repository;

import com.example.moderationplanner.entity.ModerationTrack;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ModerationTrackRepository extends JpaRepository<ModerationTrack, UUID> {
}
