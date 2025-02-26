package com.example.moderationplanner.service;

import com.example.moderationplanner.entity.Moderator;
import com.example.moderationplanner.repository.ModeratorRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.UncheckedIOException;
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

    public Moderator createModerator(String firstName, String lastName, MultipartFile file) {
        try {
            byte[] imageData = file.getBytes();
            Moderator moderator = new Moderator(null, firstName, lastName, imageData);
            return moderatorRepository.save(moderator);
        } catch (Exception e) {
            throw new UncheckedIOException("Failed to read image data", new java.io.IOException(e));
        }
    }

    public void deleteModerator(UUID id) {
        moderatorRepository.deleteById(id);
    }
}
