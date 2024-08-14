package org.teamchallenge.bookshop.service.Impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.teamchallenge.bookshop.dto.CartDto;
import org.teamchallenge.bookshop.dto.CartItemDto;
import org.teamchallenge.bookshop.dto.CartItemsResponseDto;
import org.teamchallenge.bookshop.enums.Discount;
import org.teamchallenge.bookshop.exception.BookNotFoundException;
import org.teamchallenge.bookshop.exception.CartNotFoundException;
import org.teamchallenge.bookshop.exception.NotFoundException;
import org.teamchallenge.bookshop.mapper.BookMapper;
import org.teamchallenge.bookshop.mapper.CartMapper;
import org.teamchallenge.bookshop.model.Book;
import org.teamchallenge.bookshop.model.Cart;
import org.teamchallenge.bookshop.model.User;
import org.teamchallenge.bookshop.repository.BookRepository;
import org.teamchallenge.bookshop.repository.CartRepository;
import org.teamchallenge.bookshop.service.CartService;
import org.teamchallenge.bookshop.service.UserService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final BookRepository bookRepository;
    private final UserService userService;
    private final CartMapper cartMapper;
    private final BookMapper bookMapper;

    public CartItemsResponseDto getCartItems() {
        User user = userService.getAuthenticatedUser();
        Cart cart = cartRepository.findById(user.getCart().getId())
                .orElseThrow(CartNotFoundException::new);

        List<CartItemDto> items = cart.getItems().entrySet().stream()
                .map(entry -> bookMapper.bookToCartItemDto(entry.getKey(), entry.getValue()))
                .toList();

        BigDecimal totalPrice = cart.getItems().entrySet().stream()
                .map(entry -> entry.getKey().getPrice().multiply(BigDecimal.valueOf(entry.getValue())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartItemsResponseDto(items, totalPrice);
    }


    @Override
    public CartDto getCartByUser() {
        User user = userService.getAuthenticatedUser();
        Cart cart = cartRepository.findById(user.getCart().getId()).orElseThrow(NotFoundException::new);
        return cartMapper.entityToDto(cart);
    }


    public void addBookToCart(long bookId) {
        User user = userService.getAuthenticatedUser();
        Cart cart = cartRepository.findById(user.getCart().getId())
                .orElseThrow(CartNotFoundException::new);

        Book book = bookRepository.findById(bookId)
                .orElseThrow(BookNotFoundException::new);

        cart.getItems().merge(book, 1, Integer::sum);
        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public CartDto updateQuantity( long bookId, int quantity) {
        User user = userService.getAuthenticatedUser();
        Cart cart = cartRepository.findById(user.getCart().getId()).orElseThrow(NotFoundException::new);
        Book book = bookRepository.findById(bookId).orElseThrow(NotFoundException::new);
        addOrUpdateBook(cart, book, quantity);
        cartRepository.save(cart);
        return cartMapper.entityToDto(cart);
    }

    @Override
    @Transactional
    public CartDto deleteBookFromCart(long bookId) {
        User user = userService.getAuthenticatedUser();
        Cart cart = cartRepository.findById(user.getCart().getId()).orElseThrow(NotFoundException::new);
        Book book = bookRepository.findById(bookId).orElseThrow(NotFoundException::new);
        deleteBook(cart, book);
        cartRepository.save(cart);
        return cartMapper.entityToDto(cart);
    }

    private void addOrUpdateBook(Cart cart, Book book, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Invalid quantity");
        }
        cart.getItems().merge(book, quantity, Integer::sum);
        cart.setLastModified(LocalDate.now());
    }

    private void deleteBook(Cart cart, Book book) {
        cart.getItems().remove(book);
        cart.setLastModified(LocalDate.now());
    }
    public BigDecimal calculateTotalPrice() {
        User user = userService.getAuthenticatedUser();
        Cart cart = cartRepository.findById(user.getCart().getId())
                .orElseThrow(CartNotFoundException::new);
        BigDecimal totalPrice = cart.getItems().entrySet().stream()
                .map(entry -> entry.getKey().getPrice().multiply(BigDecimal.valueOf(entry.getValue())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (cart.getDiscount() != null && cart.getDiscount() != Discount.NONE) {
            int discountPercentage = cart.getDiscount().getPercentage();
            BigDecimal discountAmount = totalPrice.multiply(BigDecimal.valueOf(discountPercentage)).divide(BigDecimal.valueOf(100));
            totalPrice = totalPrice.subtract(discountAmount);
        }
        return totalPrice;
    }
    public void applyDiscount( Discount discount) {
        User user = userService.getAuthenticatedUser();
        Cart cart = cartRepository.findById(user.getCart().getId()).orElseThrow(CartNotFoundException::new);
        cart.setDiscount(discount);
        cartRepository.save(cart);
    }
}
