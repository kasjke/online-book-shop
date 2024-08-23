package org.teamchallenge.bookshop.service;

import org.teamchallenge.bookshop.dto.PasswordResetDto;
import org.teamchallenge.bookshop.dto.ProfileUpdateDto;

public interface ProfileService {
    ProfileUpdateDto updateProfile(ProfileUpdateDto profileDto);

    ProfileUpdateDto getUserData();

    void resetPassword(PasswordResetDto resetDto);
}
