package org.teamchallenge.bookshop.dto;

import lombok.Getter;
import lombok.Setter;
import org.teamchallenge.bookshop.enums.Role;

@Getter
@Setter
public class UserTestDto {
    private String name;
    private String surname;
    private String email;
    private String phoneNumber;
    private Role role;
    private String password;
}
