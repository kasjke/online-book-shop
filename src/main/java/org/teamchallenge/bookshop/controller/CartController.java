package org.teamchallenge.bookshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.teamchallenge.bookshop.dto.CartDto;
import org.teamchallenge.bookshop.dto.CartItemsResponseDto;
import org.teamchallenge.bookshop.enums.Discount;
import org.teamchallenge.bookshop.service.CartService;

import java.math.BigDecimal;

@RestController
@RequestMapping("api/v1/cart")
@RequiredArgsConstructor
@Slf4j
public class CartController {
    private final CartService cartService;

    @GetMapping("/items")
    public ResponseEntity<CartItemsResponseDto> getCartItems() {
        CartItemsResponseDto items = cartService.getCartItems();
        return ResponseEntity.ok(items);
    }

    @Operation(summary = "Add book in cart",
            security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/add")
    public ResponseEntity<Void> addBookToCart(
            @RequestParam long bookId) {
        cartService.addBookToCart(bookId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update amount of book in cart",
            security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/update")
    public ResponseEntity<CartDto> updateBookQuantityInCart(

            @Parameter(description = "Id of book")
            @RequestParam long bookId,
            @Parameter(description = "New quantity of book")
            @RequestParam int quantity) {
        return ResponseEntity.ok(cartService.updateQuantity(bookId, quantity));
    }
    @Operation(summary = "Calculate total price",
            description = "Calculate the total price of items in the cart",
            security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/total")
    public ResponseEntity<BigDecimal> calculateTotalPrice() {
        BigDecimal totalPrice = cartService.calculateTotalPrice();
        return ResponseEntity.ok(totalPrice);
    }
    @PutMapping("/applyDiscount")
    public ResponseEntity<CartDto> applyDiscount(
            @Parameter(description = "discount of books in cart")
            @RequestParam Discount discount
            ) {
        cartService.applyDiscount(discount);
        return ResponseEntity.ok(cartService.getCartByUser());
    }

    @Operation(summary = "Delete book from cart",
            security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/delete")
    public ResponseEntity<CartDto> deleteBookFromCart(
            @Parameter(description = "Id of book")
            @RequestParam long bookId) {
        return ResponseEntity.ok(cartService.deleteBookFromCart(bookId));
    }

}