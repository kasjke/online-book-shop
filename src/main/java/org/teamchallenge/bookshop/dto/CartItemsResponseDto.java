package org.teamchallenge.bookshop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
@Getter
@AllArgsConstructor
public class CartItemsResponseDto  {
    private List<CartItemDto> items;
    private BigDecimal totalPrice;
}
