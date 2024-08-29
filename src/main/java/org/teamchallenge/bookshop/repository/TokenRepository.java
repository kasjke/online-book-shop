package org.teamchallenge.bookshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.teamchallenge.bookshop.model.Token;
import org.teamchallenge.bookshop.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    @Query("SELECT t FROM Token t WHERE t.user = :user AND t.expired = false AND t.revoked = false")
    List<Token> findAllValidTokenByUser(@Param("user") User user);
    Optional<Token> findByTokenValue(String token);

    void deleteAllByUser(User user);

    List<Token> findAllValidTokenByUserEmail(String oldEmail);
}