package com.example.moderationplanner.repository;

import com.example.moderationplanner.entity.Moderator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ModeratorRepository extends JpaRepository<Moderator, UUID> {
}
