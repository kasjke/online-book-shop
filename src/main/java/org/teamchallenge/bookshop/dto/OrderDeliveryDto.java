package org.teamchallenge.bookshop.dto;

import lombok.Getter;
import lombok.Setter;
import org.teamchallenge.bookshop.enums.DeliveryType;

@Getter
@Setter
public class OrderDeliveryDto {
    private boolean anotherRecipient;
    private boolean call;
    private String city;
    private DeliveryType deliveryType;
    private String department;
    private String email;
    private String firstName;
    private String lastName;
    private String payment;
    private String phoneNumber;
    private String recipientFirstName;
    private String recipientLastName;
    private String recipientPhoneNumber;
}
