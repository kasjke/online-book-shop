package org.teamchallenge.bookshop.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.teamchallenge.bookshop.dto.OAuth2UserInfo;
import org.teamchallenge.bookshop.model.request.AuthRequest;
import org.teamchallenge.bookshop.model.request.AuthenticationResponse;
import org.teamchallenge.bookshop.model.request.RegisterRequest;
import org.teamchallenge.bookshop.secutity.JwtService;
import org.teamchallenge.bookshop.service.AuthService;
import org.teamchallenge.bookshop.service.OAuth2Service;
import org.teamchallenge.bookshop.util.CookieUtils;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthService authService;
    private final OAuth2Service oAuth2Service;
    private final JwtService jwtService;


    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> auth(@RequestBody AuthRequest request, HttpServletResponse response) {
        AuthenticationResponse authResponse = authService.auth(request);
        CookieUtils.addCookie(response, "jwt", authResponse.getToken(), 7 * 24 * 60 * 60);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        String token = jwtService.extractTokenFromRequest(request);
        if (token != null) {
            authService.logout(token);
            CookieUtils.deleteCookie(request, response, "jwt");        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/oauth2/success")
    public ResponseEntity<AuthenticationResponse> oauth2AuthenticationSuccess(@RequestBody OAuth2UserInfo oauth2UserInfo) {
        AuthenticationResponse response = oAuth2Service.processOAuth2Authentication(oauth2UserInfo);
        return ResponseEntity.ok(response);
    }
}
