package org.teamchallenge.bookshop.service;

import org.springframework.stereotype.Service;
import org.teamchallenge.bookshop.dto.BookDto;
import org.teamchallenge.bookshop.dto.BookInCatalogDto;
import org.teamchallenge.bookshop.dto.CatalogDto;
import org.teamchallenge.bookshop.enums.Category;

import java.util.List;
import java.util.Map;

@Service
public interface BookService {

    void addBook(BookDto book);

    BookDto getBookById(Long id);

    List<BookInCatalogDto> getBooksForSlider();

    List<String> getAllCategory();

    BookDto updateBook(BookDto bookDto);

    void deleteBook(Long id);

    List<BookDto> getAllBooks();

    BookInCatalogDto getBookByTitle(String title);

    List<BookDto> getSorted(String category, String timeAdded, String price, String author, Float priceMin, Float priceMax);
}
