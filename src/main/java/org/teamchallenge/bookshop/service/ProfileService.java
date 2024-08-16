package org.teamchallenge.bookshop.service;

import org.teamchallenge.bookshop.dto.ProfileUpdateDto;
import org.teamchallenge.bookshop.model.Profile;

public interface ProfileService {
    Profile updateProfile(Long id, ProfileUpdateDto profileDto);
}
