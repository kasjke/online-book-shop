package org.teamchallenge.bookshop.service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.teamchallenge.bookshop.dto.ProfileUpdateDto;
import org.teamchallenge.bookshop.exception.NotFoundException;
import org.teamchallenge.bookshop.mapper.ProfileMapper;
import org.teamchallenge.bookshop.model.Profile;
import org.teamchallenge.bookshop.repository.ProfileRepository;
import org.teamchallenge.bookshop.service.ProfileService;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private final ProfileRepository profileRepository;
    private final ProfileMapper profileMapper;

    @Override
    public Profile updateProfile(Long id, ProfileUpdateDto profileDto) {
        Profile profile = profileRepository.findById(id)
                .orElseThrow(NotFoundException::new);

        profileMapper.updateProfileFromDto(profileDto, profile);

        return profileRepository.save(profile);
    }
}
