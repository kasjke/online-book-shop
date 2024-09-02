package org.teamchallenge.bookshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @Operation(summary = "Get items in cart",
            description = "Fetches all items currently in the user's cart",
            security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/items")
    public ResponseEntity<CartItemsResponseDto> getCartItems() {
        CartItemsResponseDto items = cartService.getCartItems();
        return ResponseEntity.ok(items);
    }

    @Operation(summary = "Add a book to cart",
            description = "Adds a book to the user's cart using the book ID",
            security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/add")
    public ResponseEntity<CartItemsResponseDto> addBookToCart(
            @Parameter(description = "ID of the book to add to the cart")
            @RequestParam long bookId) {
        return ResponseEntity.ok(cartService.addBookToCart(bookId));
    }

    @Operation(summary = "Update quantity of a book in cart",
            description = "Updates the quantity of a specific book in the user's cart",
            security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping("/update")
    public ResponseEntity<CartItemsResponseDto> updateBookQuantityInCart(
            @Parameter(description = "ID of the book to update")
            @RequestParam long bookId,
            @Parameter(description = "New quantity of the book")
            @RequestParam(required = false) Integer quantity) {
        return ResponseEntity.ok(cartService.updateQuantity(bookId,quantity));
    }

    @Operation(summary = "Calculate total price of cart",
            description = "Calculates the total price of items in the cart, applying any available discounts",
            security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/total")
    public ResponseEntity<BigDecimal> calculateTotalPrice(
            @Parameter(description = "Type of discount to apply")
            @RequestParam(value = "discount", defaultValue = "NO_DISCOUNT") Discount discount) {
        cartService.applyDiscount(discount);
        BigDecimal totalPrice = cartService.calculateTotalPriceWithDiscount();
        return ResponseEntity.ok(totalPrice);
    }

    @Operation(summary = "Delete a book from cart",
            description = "Deletes a book from the user's cart using the book ID",
            security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/delete")
    public ResponseEntity<CartItemsResponseDto> deleteBookFromCart(
            @Parameter(description = "ID of the book to delete from the cart")
            @RequestParam long bookId) {
        cartService.deleteBookFromCart(bookId);
        return ResponseEntity.ok(cartService.deleteBookFromCart(bookId));
    }
}
