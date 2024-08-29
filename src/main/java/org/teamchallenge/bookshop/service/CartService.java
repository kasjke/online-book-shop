package org.teamchallenge.bookshop.service;

import jakarta.transaction.Transactional;
import org.teamchallenge.bookshop.dto.CartDto;
import org.teamchallenge.bookshop.dto.CartItemsResponseDto;
import org.teamchallenge.bookshop.enums.Discount;

import java.math.BigDecimal;

public interface CartService {
    CartItemsResponseDto getCartItems();

    CartDto getCartByUser();

    CartItemsResponseDto addBookToCart(long bookId);

    @Transactional
    CartItemsResponseDto updateQuantity(long bookId, Integer quantity);

    @Transactional
    CartItemsResponseDto deleteBookFromCart(long bookId);

    BigDecimal calculateTotalPriceWithDiscount();

    void applyDiscount(Discount discount);
}
