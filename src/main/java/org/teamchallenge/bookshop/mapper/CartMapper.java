package org.teamchallenge.bookshop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.teamchallenge.bookshop.dto.BookDto;
import org.teamchallenge.bookshop.dto.CartDto;
import org.teamchallenge.bookshop.dto.CartItemDto;
import org.teamchallenge.bookshop.model.Book;
import org.teamchallenge.bookshop.model.Cart;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {BookMapper.class})
public interface CartMapper {

    @Mapping(target = "items", source = "items", qualifiedByName = "mapToList")
    CartDto entityToDto(Cart cart);

    @Named("mapToList")
    default List<BookDto> mapToList(Map<Book, Integer> map) {
        return map.entrySet()
                .stream()
                .map(entry -> {
                    BookDto bookDto = bookToBookDto(entry.getKey());
                    bookDto.setQuantity(entry.getValue());
                    return bookDto;
                })
                .collect(Collectors.toList());
    }

    @Named("listToMap")
    default Map<Book, Integer> listToMap(List<BookDto> list) {
        return list.stream()
                .collect(Collectors.toMap(
                        this::bookDtoToBook,
                        BookDto::getQuantity
                ));
    }
    @Named("mapCartItemsPriceToDtoWithoutQuantity")
    default List<CartItemDto> mapCartItemsPriceToDtoWithoutQuantity(Map<Book, Integer> items) {
        if (items == null) {
            return java.util.Collections.emptyList();
        }
        return items.entrySet().stream()
                .map(entry -> {
                    Book book = entry.getKey();
                    CartItemDto dto = new CartItemDto();
                    dto.setId(book.getId());
                    dto.setTitle(book.getTitle());
                    dto.setQuantity(entry.getValue());
                    dto.setPrice(book.getPrice());
                    dto.setAuthors(book.getAuthors());
                    dto.setTitleImage(book.getTitleImage());
                    return dto;
                })
                .collect(Collectors.toList());
    }
    @Named("mapCartItemsToDto")
    default List<CartItemDto> mapCartItemsToDto(Map<Book, Integer> items) {
        if (items == null) {
            return java.util.Collections.emptyList();
        }
        return items.entrySet().stream()
                .map(entry -> {
                    Book book = entry.getKey();
                    CartItemDto dto = new CartItemDto();
                    dto.setId(book.getId());
                    dto.setTitle(book.getTitle());
                    dto.setQuantity(entry.getValue());
                    dto.setPrice(book.getPrice().multiply(BigDecimal.valueOf(entry.getValue())));
                    dto.setAuthors(book.getAuthors());
                    dto.setTitleImage(book.getTitleImage());
                    return dto;
                })
                .collect(Collectors.toList());
    }
    BookDto bookToBookDto(Book book);
    Book bookDtoToBook(BookDto bookDto);
}