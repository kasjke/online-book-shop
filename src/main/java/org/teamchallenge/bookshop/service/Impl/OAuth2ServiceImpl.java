package org.teamchallenge.bookshop.service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.teamchallenge.bookshop.Oauth2.UserCreationService;
import org.teamchallenge.bookshop.dto.OAuth2UserInfo;
import org.teamchallenge.bookshop.model.User;
import org.teamchallenge.bookshop.model.request.AuthenticationResponse;
import org.teamchallenge.bookshop.repository.UserRepository;
import org.teamchallenge.bookshop.secutity.JwtService;
import org.teamchallenge.bookshop.service.OAuth2Service;

import static org.teamchallenge.bookshop.constants.ValidationConstants.UNSUPPORTED_PROVIDER;


@Service
@RequiredArgsConstructor
public class OAuth2ServiceImpl implements OAuth2Service {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final UserCreationService userCreationService;

    @Override
    public AuthenticationResponse processOAuth2Authentication(OAuth2UserInfo oauth2UserInfo) {
        User user = userRepository.findByEmail(oauth2UserInfo.getEmail())
                .map(existingUser -> {
                    existingUser.setName(oauth2UserInfo.getName());
                    existingUser.setSurname(oauth2UserInfo.getSurname());
                    existingUser.setProvider(oauth2UserInfo.getProvider());
                    existingUser.setProviderId(oauth2UserInfo.getProviderId());
                    return userRepository.save(existingUser);
                })
                .orElseGet(() -> userCreationService.createNewUser(oauth2UserInfo));

        return AuthenticationResponse.builder()
                .token(jwtService.generateJWT(user))
                .build();
    }

    @Override
    public String getLogoutUrl(String provider) {
        return switch (provider) {
            case "google" -> "https://accounts.google.com/Logout";
            case "facebook" -> "https://www.facebook.com/logout.php";
            default -> throw new IllegalArgumentException(UNSUPPORTED_PROVIDER + provider);
        };
    }

}