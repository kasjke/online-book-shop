package org.teamchallenge.bookshop.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.teamchallenge.bookshop.enums.Category;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "catalog")
public class Catalog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int quantityOfBooks;
    @OneToMany(mappedBy = "catalog", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Book> books = new ArrayList<>();
    @Enumerated(EnumType.STRING)
    private Category category;
}