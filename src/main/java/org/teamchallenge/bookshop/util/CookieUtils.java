package org.teamchallenge.bookshop.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

@Service
public class CookieUtils {
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    public static String getCookieValue(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
    public static void deleteCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
    public void addJwtCookie(HttpServletResponse response, String token) {
        CookieUtils.addCookie(response, "jwt", token, 7 * 24 * 60 * 60);
    }
    public void addJwtCookie(HttpServletResponse response, String token, boolean rememberMe) {
        int maxAge = rememberMe ? 10 * 24 * 60 * 60 : 7 * 24 * 60 * 60;
        CookieUtils.addCookie(response, "jwt", token, maxAge);
    }

    public void removeJwtCookie(HttpServletResponse response) {
        CookieUtils.deleteCookie(response, "jwt");
    }
}