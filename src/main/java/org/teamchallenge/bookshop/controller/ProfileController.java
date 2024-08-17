package org.teamchallenge.bookshop.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.teamchallenge.bookshop.dto.ProfileDto;
import org.teamchallenge.bookshop.dto.ProfileUpdateDto;
import org.teamchallenge.bookshop.mapper.ProfileMapper;
import org.teamchallenge.bookshop.model.Profile;
import org.teamchallenge.bookshop.model.User;
import org.teamchallenge.bookshop.service.ProfileService;
import org.teamchallenge.bookshop.service.UserService;

@RestController
@RequestMapping("api/v1/profile")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Profile controller", description = "API for profile management")
public class ProfileController {
    private final ProfileService profileService;
    private final ProfileMapper profileMapper;
    private final UserService userService;

    @PatchMapping("update")
    public ProfileDto updateProfile(@RequestBody ProfileUpdateDto profileUpdateDto) {
        User user = userService.getAuthenticatedUser();
        Long id = user.getProfile().getId();
        Profile updatedProfile = profileService.updateProfile(id, profileUpdateDto);
        return profileMapper.toDto(updatedProfile);
    }
}
