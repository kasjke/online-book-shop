package org.teamchallenge.bookshop.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.teamchallenge.bookshop.dto.BookCharacteristicDto;
import org.teamchallenge.bookshop.dto.BookDto;
import org.teamchallenge.bookshop.dto.BookInCatalogDto;
import org.teamchallenge.bookshop.dto.CategoryDto;

import java.util.List;


public interface BookService {

    void addBook(BookDto book);

    BookCharacteristicDto getBookById(Long id);

    List<BookInCatalogDto> getBooksForSlider();

    List<CategoryDto> getAllCategory();

    BookDto updateBook(BookDto bookDto);

    void deleteBook(Long id);

    Page<BookCharacteristicDto> getAllBooks(Pageable pageable);

    BookInCatalogDto getFirstBookByTitle(String title);

    Page<BookCharacteristicDto> getSorted(Pageable pageable,
                            Integer categoryId,
                            String timeAdded,
                            String price,
                            String author,
                            Float priceMin,
                            Float priceMax);

    Page<BookCharacteristicDto> getBooksByCharacteristics(Pageable pageable, String publisher, String language, String bookType, String coverType);

    BookCharacteristicDto createBookWithCharacteristics(BookCharacteristicDto bookCharacteristicDto);
}
