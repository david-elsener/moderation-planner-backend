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
