package org.teamchallenge.bookshop.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.teamchallenge.bookshop.dto.BookDto;
import org.teamchallenge.bookshop.model.Profile;
import org.teamchallenge.bookshop.service.ProfileService;

@RestController
@RequestMapping("api/v1/profile")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Profile controller", description = "API for profile management")
public class ProfileController {
    private final ProfileService profileService;

    @PostMapping("/add")
    public ResponseEntity<Void> addProfile(@RequestBody Profile profile) {
        profileService.addProfile(profile);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
