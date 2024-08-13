package org.teamchallenge.bookshop.enums;

import lombok.Getter;

@Getter
public enum DeliveryType {
    NOVA_POSHTA_OFFICE("НП.Відділення/поштомат"),
    UKR_POSHTA_OFFICE("Укрпошта.Відділення"),
    MEEST_OFFICE("Meest.Відділення"),
    NOVA_POSHTA_COURIER("НП.Кур'єр");

    private final String name;

    DeliveryType(String name) {
        this.name = name;
    }


}
