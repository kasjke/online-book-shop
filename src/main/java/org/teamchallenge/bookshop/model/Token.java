package org.teamchallenge.bookshop.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "token")
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token_value", unique = true, nullable = false)
    private String tokenValue;

    @Column(name = "expired", nullable = false)
    private boolean expired;

    @Column(name = "revoked", nullable = false)
    private boolean revoked;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;




    public Token(String tokenValue, LocalDateTime expiryDate) {
        this.tokenValue = tokenValue;
        this.expiryDate = expiryDate;
        this.revoked = false;
        this.expired = false;
    }
}