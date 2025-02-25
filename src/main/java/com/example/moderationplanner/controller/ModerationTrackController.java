package com.example.moderationplanner.controller;

import com.example.moderationplanner.entity.ModerationTrack;
import com.example.moderationplanner.entity.Moderator;
import com.example.moderationplanner.repository.ModerationTrackRepository;
import com.example.moderationplanner.repository.ModeratorRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/tracks")
public class ModerationTrackController {

    private final ModerationTrackRepository trackRepository;
    private final ModeratorRepository moderatorRepository;

    public ModerationTrackController(ModerationTrackRepository trackRepository, ModeratorRepository moderatorRepository) {
        this.trackRepository = trackRepository;
        this.moderatorRepository = moderatorRepository;
    }

    @GetMapping
    public List<ModerationTrack> getAllTracks() {
        return trackRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> createTrack(@RequestBody TrackDTO trackDTO) {
        Optional<Moderator> moderatorOpt = moderatorRepository.findById(UUID.fromString(trackDTO.moderatorId));

        if (moderatorOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Moderator not found");
        }

        ModerationTrack track = new ModerationTrack();
        track.setModerator(moderatorOpt.get());
        track.setChannel(trackDTO.channel);
        track.setStartTime(LocalDateTime.parse(trackDTO.startTime));
        track.setEndTime(LocalDateTime.parse(trackDTO.endTime));

        trackRepository.save(track);
        return ResponseEntity.ok(track);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTrack(@PathVariable UUID id) {
        if (!trackRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        trackRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // DTO Class for Track creation
    public static class TrackDTO {
        public String moderatorId;
        public String channel;
        public String startTime;
        public String endTime;
    }
}
