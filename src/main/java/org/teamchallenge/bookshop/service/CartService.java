package org.teamchallenge.bookshop.service;

import jakarta.transaction.Transactional;
import org.teamchallenge.bookshop.dto.CartDto;
import org.teamchallenge.bookshop.dto.CartItemsResponseDto;
import org.teamchallenge.bookshop.enums.Discount;

import java.math.BigDecimal;

public interface CartService {
    CartItemsResponseDto getCartItems();

    CartDto getCartByUser();

    void addBookToCart(long bookId);

    @Transactional
    CartDto updateQuantity(long bookId, int quantity);

    @Transactional
    CartDto deleteBookFromCart(long bookId);

    BigDecimal calculateTotalPrice();

    void applyDiscount(Discount discount);
}
