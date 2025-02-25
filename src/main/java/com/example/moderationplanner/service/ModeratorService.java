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
