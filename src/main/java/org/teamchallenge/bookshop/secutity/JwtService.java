package org.teamchallenge.bookshop.secutity;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.teamchallenge.bookshop.exception.SecretKeyNotFoundException;
import org.teamchallenge.bookshop.model.Token;
import org.teamchallenge.bookshop.model.User;
import org.teamchallenge.bookshop.repository.TokenRepository;
import org.teamchallenge.bookshop.util.CookieUtils;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.teamchallenge.bookshop.constants.ValidationConstants.INVALID_JWT_TOKEN;

@Service
public class JwtService {

    private final TokenRepository tokenRepository;
    private final SecretKey signingKey;

    private static final String SECRET_KEY = Optional.ofNullable(System.getenv("SECRET_KEY"))
            .orElseThrow(SecretKeyNotFoundException::new);

    private static final long REMEMBER_ME_EXPIRATION_TIME =1000 * 60 * 60 * 24 * 10 ;
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7;

    @Autowired
    public JwtService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUserByEmailOrPhone(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        if (claims.containsKey("email")) {
            return claims.get("email", String.class);
        } else if (claims.containsKey("phoneNumber")) {
            return claims.get("phoneNumber", String.class);
        } else {
            return claims.getSubject();
        }
    }

    public String extractProviderFromToken(String token) {
        if (isJwt(token)) {
            return "jwt";
        } else if (token.startsWith("ya29.")) {
            return "google";
        } else if (token.startsWith("EAAB")) {
            return "facebook";
        }
        return null;
    }

    private boolean isJwt(String token) {
        String[] parts = token.split("\\.");
        return parts.length == 3;
    }

    private Map<String, Object> generateClaims(User user) {
        Map<String, Object> claims = new HashMap<>();
        if (user.getEmail() != null) {
            claims.put("email", user.getEmail());
        }
        if (user.getPhoneNumber() != null) {
            claims.put("phoneNumber", user.getPhoneNumber());
        }
        claims.put("cartId", user.getCart().getId());
        return claims;
    }

    public String generateJWT(User user) {
        return generateJWT(user, false);
    }

    public String generateJWT(User user, boolean rememberMe) {
        Map<String, Object> claims = generateClaims(user);
        String subject = user.getEmail() != null ? user.getEmail() : user.getPhoneNumber();
        long expirationTime = rememberMe ? REMEMBER_ME_EXPIRATION_TIME : EXPIRATION_TIME;

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(signingKey)
                .compact();
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public String extractTokenFromRequest(HttpServletRequest request) {
        return CookieUtils.getCookieValue(request, "jwt");
    }

    public Token blacklistToken(String jwt) {
        try {
            Claims payload = Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(jwt).getPayload();
            LocalDateTime expiryDate = payload.getExpiration().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            Token token = new Token(jwt, expiryDate);
            token.setRevoked(true);

            return tokenRepository.save(token);
        } catch (JwtException e) {
            throw new IllegalArgumentException(INVALID_JWT_TOKEN, e);
        }
    }

    public boolean isTokenBlacklisted(String jwt) {
        return tokenRepository.findByTokenValueAndRevokedTrue(jwt).isPresent();
    }
}