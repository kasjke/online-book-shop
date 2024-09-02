package org.teamchallenge.bookshop.enums;

import lombok.Getter;

@Getter
public enum Discount {
    NO_DISCOUNT(0),
    DISCOUNT_5_PERCENT(5),
    DISCOUNT_10_PERCENT(10),
    DISCOUNT_15_PERCENT(15);

    private final int percentage;

    Discount(int percentage) {
        this.percentage = percentage;
    }
}
