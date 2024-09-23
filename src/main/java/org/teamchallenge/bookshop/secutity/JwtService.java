package org.teamchallenge.bookshop.secutity;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.util.StringUtils;
import org.teamchallenge.bookshop.exception.SecretKeyNotFoundException;
import org.teamchallenge.bookshop.exception.UserNotFoundException;
import org.teamchallenge.bookshop.model.Token;
import org.teamchallenge.bookshop.model.User;
import org.teamchallenge.bookshop.repository.TokenRepository;
import org.teamchallenge.bookshop.repository.UserRepository;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.*;

import static org.teamchallenge.bookshop.constants.ValidationConstants.ACCESS_TOKEN_NOT_FOUND;
import static org.teamchallenge.bookshop.constants.ValidationConstants.REFRESH_TOKEN_NOT_FOUND;

@Service
public class JwtService {
    private final TokenRepository tokenRepository;
    private final SecretKey signingKey;
    private final UserRepository userRepository;

    private static final String SECRET_KEY = Optional.ofNullable(System.getenv("SECRET_KEY"))
            .orElseThrow(SecretKeyNotFoundException::new);

    private static final long ACCESS_EXPIRATION_TOKEN = Long.parseLong(
            Optional.ofNullable(System.getenv("ACCESS_EXPIRATION_TOKEN")).orElse(ACCESS_TOKEN_NOT_FOUND)
    );

    private static final long REFRESH_EXPIRATION_TOKEN = Long.parseLong(
            Optional.ofNullable(System.getenv("REFRESH_EXPIRATION_TOKEN")).orElse(REFRESH_TOKEN_NOT_FOUND)
    );


    @Autowired
    public JwtService(TokenRepository tokenRepository, UserRepository userRepository) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
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
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        if (user.getProviderId() != null) {
            claims.put("providerId", user.getProviderId());
        }
        claims.put("email", user.getEmail());
        claims.put("role", user.getRole().name());
        return Jwts.builder()
                .claims(claims)
                .subject(getUserIdentifier(user))
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(signingKey)
                .compact();
    }

    private String getUserIdentifier(User user) {
        return user.getId() == 0 ? user.getProviderId() : String.valueOf(user.getId());
    }

    public Long extractUserId(String token) {
        Claims claims = Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token).getPayload();
        String userIdentifier = claims.getSubject();

        if (userIdentifier.matches("\\d+")) {
            return Long.parseLong(userIdentifier);
        } else {
            return userRepository.findByProviderId(userIdentifier)
                    .map(User::getId)
                    .orElseGet(() ->
                            userRepository.findByEmail(userIdentifier)
                                    .map(User::getId)
                                    .orElseThrow(UserNotFoundException::new)
                    );
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