package org.teamchallenge.bookshop.dto;

import jakarta.validation.constraints.NotNull;
import org.teamchallenge.bookshop.enums.Role;

import java.util.List;

public record ProfileDto
        (
                String firstName,
                String lastName,
                String email,
                String phoneNumber) {
}
