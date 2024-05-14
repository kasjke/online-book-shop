package org.teamchallenge.bookshop.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.teamchallenge.bookshop.enums.Available;
import org.teamchallenge.bookshop.enums.Category;
import org.teamchallenge.bookshop.model.Author;

import java.math.BigDecimal;
import java.util.List;

public record BookInCatalogDto(
        long id,
        String title,
        String fullDescription,
        String shortDescription,
        BigDecimal price,
        Category category,
        Available available,
        String imageUrl,
        @JsonIgnore
        List<Author> authors
) {}
