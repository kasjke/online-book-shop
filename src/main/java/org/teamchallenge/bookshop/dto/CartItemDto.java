package org.teamchallenge.bookshop.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class CartItemDto {
    private long id;
    private String title;
    private String category;
    private String authors;
    private String titleImage;
    private int quantity;
}
