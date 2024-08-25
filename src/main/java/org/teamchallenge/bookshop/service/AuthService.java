package org.teamchallenge.bookshop.service;

import jakarta.servlet.http.HttpServletRequest;
import org.teamchallenge.bookshop.model.request.AuthRequest;
import org.teamchallenge.bookshop.model.request.AuthenticationResponse;
import org.teamchallenge.bookshop.model.request.RefreshTokenRequest;
import org.teamchallenge.bookshop.model.request.RegisterRequest;

public interface AuthService {
    AuthenticationResponse register(RegisterRequest registerRequest);

    AuthenticationResponse auth(AuthRequest authRequest);

    void logout(HttpServletRequest request);

    AuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest);
}
