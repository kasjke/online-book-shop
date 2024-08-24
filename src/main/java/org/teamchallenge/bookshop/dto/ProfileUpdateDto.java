package org.teamchallenge.bookshop.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileUpdateDto {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
}
