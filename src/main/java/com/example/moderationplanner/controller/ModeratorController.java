package com.example.moderationplanner.controller;

import com.example.moderationplanner.entity.Moderator;
import com.example.moderationplanner.service.ModeratorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/moderators")
public class ModeratorController {

    private final ModeratorService moderatorService;

    public ModeratorController(ModeratorService moderatorService) {
        this.moderatorService = moderatorService;
    }

    @GetMapping
    public ResponseEntity<List<ModeratorDTO>> getAllModerators() {
        List<ModeratorDTO> moderators = moderatorService.getAllModerators().stream()
                .map(moderator -> new ModeratorDTO(
                        moderator.getId(),
                        moderator.getFirstName(),
                        moderator.getLastName(),
                        "data:image/jpeg;base64," + Base64Utils.encodeToString(moderator.getImageData())
                ))
                .toList();
        return ResponseEntity.ok(moderators);
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Moderator> createModerator(
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("image") MultipartFile file) {

        Moderator createdModerator = moderatorService.createModerator(firstName, lastName, file);
        return new ResponseEntity<>(createdModerator, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteModerator(@PathVariable UUID id) {
        moderatorService.deleteModerator(id);
        return ResponseEntity.noContent().build();
    }

    // DTO class for returning Base64 encoded images as a Record
    public record ModeratorDTO(UUID id, String firstName, String lastName, String imageData) {}
}
