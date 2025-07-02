package com.velazco.velazco_backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.velazco.velazco_backend.dto.profile.response.ProfileDto;
import com.velazco.velazco_backend.entities.User;
import com.velazco.velazco_backend.services.ProfileService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    @GetMapping
    public ResponseEntity<ProfileDto> getProfile(@AuthenticationPrincipal User user) {
        if (user == null) {
            throw new EntityNotFoundException("User not found");
        }
        ProfileDto profile = profileService.getProfile(user);
        return ResponseEntity.ok(profile);
    }
}
