package org.teamchallenge.bookshop.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
@AllArgsConstructor
public class CartItemDto {
    private long id;
    private String title;
    private String authors;
    private String titleImage;
    private int quantity;
    private BigDecimal price;
}
