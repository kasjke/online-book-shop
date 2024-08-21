package org.teamchallenge.bookshop.service;

import jakarta.servlet.http.HttpServletResponse;
import org.teamchallenge.bookshop.dto.OAuth2UserInfo;
import org.teamchallenge.bookshop.model.request.AuthenticationResponse;
public interface OAuth2Service {
    AuthenticationResponse processOAuth2Authentication(OAuth2UserInfo oauth2UserInfo, HttpServletResponse response);
}
