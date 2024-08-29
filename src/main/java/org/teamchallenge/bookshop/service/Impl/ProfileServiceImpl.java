package org.teamchallenge.bookshop.service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.teamchallenge.bookshop.dto.PasswordResetDto;
import org.teamchallenge.bookshop.dto.ProfileUpdateDto;
import org.teamchallenge.bookshop.exception.WrongPasswordException;
import org.teamchallenge.bookshop.mapper.ProfileMapper;
import org.teamchallenge.bookshop.mapper.UserMapper;
import org.teamchallenge.bookshop.model.Profile;
import org.teamchallenge.bookshop.model.User;
import org.teamchallenge.bookshop.model.request.UpdateEmailResponse;
import org.teamchallenge.bookshop.repository.ProfileRepository;
import org.teamchallenge.bookshop.repository.UserRepository;
import org.teamchallenge.bookshop.secutity.JwtService;
import org.teamchallenge.bookshop.service.ProfileService;
import org.teamchallenge.bookshop.service.UserService;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private final ProfileRepository profileRepository;
    private final ProfileMapper profileMapper;
    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtService jwtService;

    @Override
    public ProfileUpdateDto updateProfile(ProfileUpdateDto profileUpdateDto) {
        User user = userService.getAuthenticatedUser();

        Profile profile = user.getProfile();
        if (profile == null) {
            profile = new Profile();
            profile.setUser(user);
            user.setProfile(profile);
        }
        if (profileUpdateDto.getFirstName() == null) {
            profileUpdateDto.setFirstName(user.getName());
        }
        if (profileUpdateDto.getLastName() == null) {
            profileUpdateDto.setLastName(user.getSurname());
        }
        if (profileUpdateDto.getPhoneNumber() == null) {
            profileUpdateDto.setPhoneNumber(user.getPhoneNumber());
        }

        profileMapper.updateProfileFromDto(profileUpdateDto, profile);
        userMapper.updateUserFromProfileDto(profileUpdateDto, user);
        Profile savedProfile = profileRepository.save(profile);
      userRepository.save(user);

        return profileMapper.toProfileUpdateDto(savedProfile);
    }
    @Override
    public ProfileUpdateDto getUserData() {
        User user = userService.getAuthenticatedUser();
        return profileMapper.toProfileUpdateDto(user);
    }

    @Override
    public void resetPassword(PasswordResetDto resetDto) {
        User user = userService.getAuthenticatedUser();
        if(passwordEncoder.matches(resetDto.getOldPassword(), user.getPassword())) {
            user.setPassword(passwordEncoder.encode(resetDto.getNewPassword()));
            userRepository.save(user);
        } else {
            throw new WrongPasswordException();
        }
    }

    public UpdateEmailResponse updateEmail(String email) {
        User user =userService.getAuthenticatedUser();

        user.setEmail(email);
        userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        jwtService.revokeAllUserTokens(user);
        jwtService.saveUserToken(user, accessToken);

        return UpdateEmailResponse.builder()
                .newEmail(user.getEmail())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}


