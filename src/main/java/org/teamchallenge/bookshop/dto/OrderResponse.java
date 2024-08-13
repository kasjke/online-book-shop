package org.teamchallenge.bookshop.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderResponse {
    private String orderNumber;
    private String trackingNumber;

    public OrderResponse(String orderNumber, String trackingNumber) {
        this.orderNumber = orderNumber;
        this.trackingNumber = trackingNumber;
    }
}
