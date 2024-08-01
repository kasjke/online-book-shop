package org.teamchallenge.bookshop.service;

import jakarta.transaction.Transactional;
import org.teamchallenge.bookshop.dto.CartDto;
import org.teamchallenge.bookshop.enums.Discount;
import org.teamchallenge.bookshop.model.Cart;

import java.math.BigDecimal;
import java.util.UUID;

public interface CartService {
    Cart createCart();

    CartDto getCartById(UUID id);

    UUID getCartIdByUserEmail(String email);

    CartDto addBookToCart(UUID cartId, long bookId);

    @Transactional
    CartDto updateQuantity(UUID id, long bookId, int quantity);

    @Transactional
    CartDto deleteBookFromCart(UUID id, long bookId);

    BigDecimal calculateTotalPrice(UUID cartId);

    void applyDiscount(UUID id, Discount discount);
}
