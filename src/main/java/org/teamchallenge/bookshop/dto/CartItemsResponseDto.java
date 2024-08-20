package org.teamchallenge.bookshop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class CartItemsResponseDto  {
    private List<CartItemDto> items;
    private BigDecimal totalPrice;
}
