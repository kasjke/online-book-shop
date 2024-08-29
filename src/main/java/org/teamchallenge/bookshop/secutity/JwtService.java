package org.teamchallenge.bookshop.secutity;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.teamchallenge.bookshop.exception.SecretKeyNotFoundException;
import org.teamchallenge.bookshop.model.Token;
import org.teamchallenge.bookshop.model.User;
import org.teamchallenge.bookshop.repository.TokenRepository;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.teamchallenge.bookshop.constants.ValidationConstants.ACCESS_TOKEN_NOT_FOUND;
import static org.teamchallenge.bookshop.constants.ValidationConstants.REFRESH_TOKEN_NOT_FOUND;

@Service
public class JwtService {
    private final TokenRepository tokenRepository;
    private final SecretKey signingKey;

    private static final String SECRET_KEY = Optional.ofNullable(System.getenv("SECRET_KEY"))
            .orElseThrow(SecretKeyNotFoundException::new);

    private static final long ACCESS_EXPIRATION_TOKEN = Long.parseLong(
            Optional.ofNullable(System.getenv("ACCESS_EXPIRATION_TOKEN")).orElse(ACCESS_TOKEN_NOT_FOUND)
    );

    private static final long REFRESH_EXPIRATION_TOKEN = Long.parseLong(
            Optional.ofNullable(System.getenv("REFRESH_EXPIRATION_TOKEN")).orElse(REFRESH_TOKEN_NOT_FOUND)
    );


    @Autowired
    public JwtService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(User user) {
        return buildToken(user, ACCESS_EXPIRATION_TOKEN);
    }

    public String generateRefreshToken(User user) {
        return buildToken(user, REFRESH_EXPIRATION_TOKEN);
    }

    private String buildToken(User user, long expiration) {
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("userId", user.getId())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(signingKey)
                .compact();
    }

    public String extractUsername(String token) {
        try {
            return Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token).getPayload().getSubject();
        } catch (JwtException e) {
            return null;
        }
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token);
            return tokenRepository.findByTokenValue(token)
                    .map(storedToken -> {
                        boolean notRevoked = !storedToken.isRevoked();
                        boolean notExpired = !storedToken.isExpired();
                        boolean notPassedExpiryDate = storedToken.getExpiryDate().isAfter(LocalDateTime.now());
                        return notRevoked && notExpired && notPassedExpiryDate;
                    })
                    .orElse(false);
        } catch (JwtException e) {
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            Date expiration = Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration();
            return expiration.before(new Date());
        } catch (JwtException e) {
            return true;
        }
    }

    public void saveUserToken(User user, String jwtToken) {
        Token token = Token.builder()
                .user(user)
                .tokenValue(jwtToken)
                .expired(false)
                .revoked(false)
                .expiryDate(LocalDateTime.now().plusMinutes(15))
                .build();
        tokenRepository.save(token);
    }

    public void revokeAllUserTokens(User user) {
        List<Token> validUserTokens = tokenRepository.findAllValidTokenByUser(user);
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }


    public String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }


}