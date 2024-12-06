package org.teamchallenge.bookshop.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.teamchallenge.bookshop.constants.ValidationConstants;
import org.teamchallenge.bookshop.dto.BookCharacteristicDto;
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

    @Mapping(target = "characteristicDto.publisher", source = "characteristic.publisher")
    @Mapping(target = "characteristicDto.language", source = "characteristic.language")
    @Mapping(target = "characteristicDto.bookType", source = "characteristic.bookType")
    @Mapping(target = "characteristicDto.coverType", source = "characteristic.coverType")
    BookCharacteristicDto entityToBookCharacteristicDto(Book book);

    CartItemDto bookToCartItemDto(Book book, int quantity);

    @Mapping(target = "characteristic.publisher", source = "characteristicDto.publisher")
    @Mapping(target = "characteristic.language", source = "characteristicDto.language")
    @Mapping(target = "characteristic.bookType", source = "characteristicDto.bookType")
    @Mapping(target = "characteristic.coverType", source = "characteristicDto.coverType")
    Book bookCharacteristicDtoToEntity(BookCharacteristicDto dto);



}
