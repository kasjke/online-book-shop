package org.teamchallenge.bookshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.teamchallenge.bookshop.model.Token;
import org.teamchallenge.bookshop.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    List<Token> findAllValidTokenByUser(User user);
    Optional<Token> findByTokenValue(String token);
    boolean existsByTokenValueAndRevokedFalseAndExpiredFalse(String token);
}