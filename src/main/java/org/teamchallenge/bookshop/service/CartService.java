package org.teamchallenge.bookshop.service;

import jakarta.transaction.Transactional;
import org.teamchallenge.bookshop.dto.CartDto;
import org.teamchallenge.bookshop.dto.CartItemDto;
import org.teamchallenge.bookshop.enums.Discount;

import java.math.BigDecimal;
import java.util.List;

public interface CartService {
    List<CartItemDto> getCartItems();

    CartDto getCartById();

    CartDto addBookToCart(long bookId);

    @Transactional
    CartDto updateQuantity(long bookId, int quantity);

    @Transactional
    CartDto deleteBookFromCart(long bookId);

    BigDecimal calculateTotalPrice();

    void applyDiscount(Discount discount);
}
