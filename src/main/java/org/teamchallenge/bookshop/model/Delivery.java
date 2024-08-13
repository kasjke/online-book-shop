package org.teamchallenge.bookshop.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.teamchallenge.bookshop.enums.DeliveryType;

@Getter
@Setter
@Entity
@Table(name = "delivery")
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private boolean anotherRecipient;
    private boolean call;
    private String city;
    private DeliveryType deliveryType;
    private String department;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String recipientFirstName;
    private String recipientLastName;
    private String recipientPhoneNumber;

    @OneToOne(mappedBy = "delivery")
    private Order order;
}
