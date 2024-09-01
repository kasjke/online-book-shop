package org.teamchallenge.bookshop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.teamchallenge.bookshop.constants.ValidationConstants;
import org.teamchallenge.bookshop.dto.BookDto;
import org.teamchallenge.bookshop.dto.BookInCatalogDto;
import org.teamchallenge.bookshop.dto.CartItemDto;
import org.teamchallenge.bookshop.enums.Category;
import org.teamchallenge.bookshop.exception.WrongEnumConstantException;
import org.teamchallenge.bookshop.model.Book;

@Mapper(componentModel = "spring")
public interface BookMapper {
    @Mapping(target = "category", source = "category", qualifiedByName = "categoryToString")
    BookDto entityToDTO(Book book);

    @Mapping(target = "category", source = "category", qualifiedByName = "categoryToString")
    @Mapping(target = "quantity", ignore = true)
    BookInCatalogDto entityToBookCatalogDTO(Book book);

    @Mapping(target = "category", source = "category", qualifiedByName = "stringToCategory")
    Book dtoToEntity(BookDto bookDto);
    @Named("categoryToString")
    default String categoryToString(Category category) {
        return category.getName();
    }

    @Named("stringToCategory")
    default Category stringToCategory(String categoryName) {
        for (Category category : Category.values()) {
            if (category.getName().equalsIgnoreCase(categoryName)) {
                return category;
            }
        }
        throw new WrongEnumConstantException(ValidationConstants.WRONG_ENUM_CONSTANT);
    }
    @Mapping(target = "id", source = "book.id")
    @Mapping(target = "title", source = "book.title")
    @Mapping(target = "quantity", source = "quantity")
    @Mapping(target = "price", source = "book.price")
    CartItemDto bookToCartItemDto(Book book, int quantity);



}
