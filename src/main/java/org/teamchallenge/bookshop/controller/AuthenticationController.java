package org.teamchallenge.bookshop.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.teamchallenge.bookshop.dto.OAuth2UserInfo;
import org.teamchallenge.bookshop.model.request.AuthRequest;
import org.teamchallenge.bookshop.model.request.AuthenticationResponse;
import org.teamchallenge.bookshop.model.request.RegisterRequest;
import org.teamchallenge.bookshop.service.AuthService;
import org.teamchallenge.bookshop.service.OAuth2Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthService authService;
    private final OAuth2Service oAuth2Service;



    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request,
            HttpServletResponse response) {
        return ResponseEntity.ok(authService.register(request, response));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> auth(
            @RequestBody AuthRequest request,
            HttpServletResponse response) {
        return ResponseEntity.ok(authService.auth(request, response));
    }
    @GetMapping("/isAuth")
    public ResponseEntity<Map<String, Boolean>> checkAuthStatus(HttpServletRequest request) {
        boolean isAuthenticated = request.getCookies() != null &&
                                  Arrays.stream(request.getCookies())
                                          .anyMatch(cookie -> "jwt".equals(cookie.getName()));
        return ResponseEntity.ok(Collections.singletonMap("isAuthenticated", isAuthenticated));
    }
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logout(request, response);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/oauth2/success")
    public ResponseEntity<AuthenticationResponse> oauth2AuthenticationSuccess(@RequestBody OAuth2UserInfo oauth2UserInfo,  HttpServletResponse response) {
        AuthenticationResponse responses = oAuth2Service.processOAuth2Authentication(oauth2UserInfo,response);
        return ResponseEntity.ok(responses);
    }
}
