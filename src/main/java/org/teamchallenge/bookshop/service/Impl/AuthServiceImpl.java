package org.teamchallenge.bookshop.service.Impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.teamchallenge.bookshop.enums.Role;
import org.teamchallenge.bookshop.exception.InvalidTokenException;
import org.teamchallenge.bookshop.exception.UserAlreadyExistsException;
import org.teamchallenge.bookshop.exception.UserNotFoundException;
import org.teamchallenge.bookshop.model.Cart;
import org.teamchallenge.bookshop.model.User;
import org.teamchallenge.bookshop.model.request.AuthRequest;
import org.teamchallenge.bookshop.model.request.AuthenticationResponse;
import org.teamchallenge.bookshop.model.request.RefreshTokenRequest;
import org.teamchallenge.bookshop.model.request.RegisterRequest;
import org.teamchallenge.bookshop.repository.CartRepository;
import org.teamchallenge.bookshop.repository.TokenRepository;
import org.teamchallenge.bookshop.repository.UserRepository;
import org.teamchallenge.bookshop.secutity.JwtService;
import org.teamchallenge.bookshop.service.AuthService;
import org.teamchallenge.bookshop.service.SendMailService;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final CartRepository cartRepository;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    private final SendMailService sendMailService;

    @Override
    @Transactional
    public AuthenticationResponse register(RegisterRequest registerRequest) {
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException();
        }
        User user = new User();
        user.setName(registerRequest.getFirstName());
        user.setSurname(registerRequest.getSurname());
        user.setEmail(registerRequest.getEmail());
        user.setPhoneNumber(registerRequest.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(Role.USER);

        Cart cart = new Cart();
        cart.setIsPermanent(true);
        cart.setLastModified(LocalDate.now());
        cartRepository.save(cart);
        user.setCart(cart);
        userRepository.save(user);
        sendMailService.sendSuccessRegistrationEmail(registerRequest.getEmail());

        String accessToken = jwtService.generateAccessToken(user);
        if (accessToken == null) {
            throw new IllegalStateException("Access token generation failed");
        }
        String refreshToken = jwtService.generateRefreshToken(user);
        if (refreshToken == null) {
            throw new IllegalStateException("Refresh token generation failed");
        }

        jwtService.saveUserToken(user, accessToken);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }


    @Override
    public AuthenticationResponse auth(AuthRequest authRequest) {
        User user = userRepository.findByEmailOrPhoneNumber(authRequest.getEmailOrPhone())
                .orElseThrow(UserNotFoundException::new);

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getEmailOrPhone(),
                        authRequest.getPassword()
                )
        );

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        jwtService.revokeAllUserTokens(user);
        jwtService.saveUserToken(user, accessToken);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.getRefreshToken();

        if (refreshToken == null) {
            throw new InvalidTokenException();
        }

        final Long userId = jwtService.extractUserId(refreshToken);

        if (userId == null) {
            throw new InvalidTokenException();
        }
        if (!jwtService.isTokenExpired(refreshToken)) {
            return userRepository.findById(userId)
                    .map(user -> {
                        String newAccessToken = jwtService.generateAccessToken(user);
                        String newRefreshToken = jwtService.generateRefreshToken(user);
                        jwtService.revokeAllUserTokens(user);
                        jwtService.saveUserToken(user, newAccessToken);
                        jwtService.saveUserToken(user, newRefreshToken);
                        return AuthenticationResponse.builder()
                                .accessToken(newAccessToken)
                                .refreshToken(newRefreshToken)
                                .build();
                    })
                    .orElseThrow(InvalidTokenException::new);
        }
        throw new InvalidTokenException();
    }


    @Override
    public void logout(HttpServletRequest request) {
        String jwt = jwtService.extractTokenFromRequest(request);
        var storedToken = tokenRepository.findByTokenValue(jwt)
                .orElse(null);
        if (storedToken != null) {
            storedToken.setExpired(true);
            storedToken.setRevoked(true);
            tokenRepository.save(storedToken);
        }
    }


}



