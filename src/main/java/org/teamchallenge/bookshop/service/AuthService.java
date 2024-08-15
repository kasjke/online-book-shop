package org.teamchallenge.bookshop.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.teamchallenge.bookshop.model.request.AuthRequest;
import org.teamchallenge.bookshop.model.request.AuthenticationResponse;
import org.teamchallenge.bookshop.model.request.RegisterRequest;

public interface AuthService {
    AuthenticationResponse register(RegisterRequest registerRequest, HttpServletResponse response);

    AuthenticationResponse auth(AuthRequest authRequest, HttpServletResponse response);

    void logout(HttpServletRequest request, HttpServletResponse response);
}
