package org.teamchallenge.bookshop.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.teamchallenge.bookshop.dto.BookDto;
import org.teamchallenge.bookshop.dto.ProfileUpdateDto;
import org.teamchallenge.bookshop.model.Profile;
import org.teamchallenge.bookshop.service.ProfileService;

@RestController
@RequestMapping("api/v1/profile")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Profile controller", description = "API for profile management")
public class ProfileController {
    private final ProfileService profileService;

    @PatchMapping("update/{id}")
    public ResponseEntity<Profile> updateProfile(@PathVariable Long id, @RequestBody ProfileUpdateDto profileUpdateDto) {
        Profile updatedProfile = profileService.updateProfile(id, profileUpdateDto);
        return ResponseEntity.ok(updatedProfile);
    }
}
