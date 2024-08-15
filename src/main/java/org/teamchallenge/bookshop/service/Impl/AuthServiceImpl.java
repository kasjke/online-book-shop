package org.teamchallenge.bookshop.service.Impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.teamchallenge.bookshop.enums.Role;
import org.teamchallenge.bookshop.exception.UserAlreadyExistsException;
import org.teamchallenge.bookshop.exception.UserNotFoundException;
import org.teamchallenge.bookshop.model.Cart;
import org.teamchallenge.bookshop.model.Token;
import org.teamchallenge.bookshop.model.User;
import org.teamchallenge.bookshop.model.request.AuthRequest;
import org.teamchallenge.bookshop.model.request.AuthenticationResponse;
import org.teamchallenge.bookshop.model.request.RegisterRequest;
import org.teamchallenge.bookshop.repository.CartRepository;
import org.teamchallenge.bookshop.repository.TokenRepository;
import org.teamchallenge.bookshop.repository.UserRepository;
import org.teamchallenge.bookshop.secutity.JwtService;
import org.teamchallenge.bookshop.service.AuthService;
import org.teamchallenge.bookshop.service.SendMailService;
import org.teamchallenge.bookshop.util.CookieUtils;

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
    private final CookieUtils cookieUtils;
    private final RestTemplate restTemplate;

    @Override
    public AuthenticationResponse register(RegisterRequest registerRequest, HttpServletResponse response) {
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

        String token = jwtService.generateJWT(user);
        cookieUtils.addJwtCookie(response, token);

        return AuthenticationResponse.builder()
                .token(token)
                .build();
    }

    @Override
    public AuthenticationResponse auth(AuthRequest authRequest,HttpServletResponse response) {
        User user = userRepository.findByEmailOrPhoneNumber(authRequest.getEmailOrPhone())
                .orElseThrow(UserNotFoundException::new);

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authRequest.getEmailOrPhone(),
                        authRequest.getPassword()
                )
        );
        String token = jwtService.generateJWT(user);
        cookieUtils.addJwtCookie(response, token);

        return AuthenticationResponse.builder()
                .token(token)
                .build();
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String token = jwtService.extractTokenFromRequest(request);
        if (token != null) {
            String provider = jwtService.extractProviderFromToken(token);
            switch (provider) {
                case "jwt":
                    handleJwtLogout(token);
                    break;
                case "google":
                    handleGoogleLogout(token);
                    break;
                case "facebook":
                    handleFacebookLogout(token);
                    break;
                default:
                    throw new RuntimeException("Unsupported token provider");
            }
            cookieUtils.removeJwtCookie(response);
        }
    }

    private void handleJwtLogout(String token) {
        Token blacklistedToken = jwtService.blacklistToken(token);
        tokenRepository.save(blacklistedToken);
    }

    private void handleGoogleLogout(String token) {
        String revokeEndpoint = "https://accounts.google.com/o/oauth2/revoke?token=" + token;
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(revokeEndpoint, String.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Failed to revoke Google token");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error during Google logout", e);
        }
    }

    private void handleFacebookLogout(String token) {
        String revokeEndpoint = "https://graph.facebook.com/v20.0/me/permissions?access_token=" + token;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(revokeEndpoint, HttpMethod.DELETE, entity, String.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Failed to revoke Facebook token");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error during Facebook logout", e);
        }
    }

}



