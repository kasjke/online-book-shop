package org.teamchallenge.bookshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.teamchallenge.bookshop.dto.CartDto;
import org.teamchallenge.bookshop.dto.CartItemDto;
import org.teamchallenge.bookshop.enums.Discount;
import org.teamchallenge.bookshop.service.CartService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("api/v1/cart")
@RequiredArgsConstructor
@Slf4j
public class CartController {
    private final CartService cartService;

    @GetMapping("/items")
    public ResponseEntity<List<CartItemDto>> getCartItems() {
        List<CartItemDto> items = cartService.getCartItems();
        return ResponseEntity.ok(items);
    }

    @Operation(summary = "Get cart by id",
            security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/findById")
    public ResponseEntity<CartDto> getCartById() {
        return ResponseEntity.ok(cartService.getCartById());
    }

    @Operation(summary = "Add book in cart",
            security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/add")
    public ResponseEntity<CartDto> addBookToCart(
            @Parameter(description = "Id of book")
            @RequestParam long bookId) {
        return ResponseEntity.ok(cartService.addBookToCart(bookId));
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
        return ResponseEntity.ok(cartService.getCartById());
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