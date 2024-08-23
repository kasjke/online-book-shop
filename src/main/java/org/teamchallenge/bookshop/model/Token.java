package org.teamchallenge.bookshop.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String tokenValue;

    @Enumerated(EnumType.STRING)
    private TokenType tokenType = TokenType.BEARER;

    private boolean revoked;

    private boolean expired;

    private LocalDateTime expiryDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;



    public enum TokenType {
        BEARER
    }

    public Token(String tokenValue, LocalDateTime expiryDate, User user) {
        this.tokenValue = tokenValue;
        this.expiryDate = expiryDate;
        this.user = user;
        this.revoked = false;
        this.expired = false;
        this.tokenType = TokenType.BEARER;
    }
}