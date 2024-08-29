package org.teamchallenge.bookshop.service;

import org.teamchallenge.bookshop.dto.ProfileUpdateDto;
import org.teamchallenge.bookshop.model.request.UpdateEmailResponse;

public interface ProfileService {
    ProfileUpdateDto updateProfile(ProfileUpdateDto profileDto);

    ProfileUpdateDto getUserData();

    void resetPassword(String password);

    UpdateEmailResponse updateEmail(String email);
}
