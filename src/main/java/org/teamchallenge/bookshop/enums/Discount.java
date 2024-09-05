package org.teamchallenge.bookshop.enums;

import lombok.Getter;

@Getter
public enum Discount {
    NODISCOUNT(0),
    DISCOUNT5PERCENT(5),
    DISCOUNT10PERCENT(10),
    DISCOUNT15PERCENT(15);

    private final int percentage;

    Discount(int percentage) {
        this.percentage = percentage;
    }
}
