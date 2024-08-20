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
    CartItemsResponseDto updateQuantity(long bookId, int quantity);

    @Transactional
    void deleteBookFromCart(long bookId);

    BigDecimal calculateTotalPriceWithDiscount();

    void applyDiscount(Discount discount);
}
