package org.teamchallenge.bookshop.service;

import org.teamchallenge.bookshop.dto.BookDto;
import org.teamchallenge.bookshop.dto.UserDto;
import org.teamchallenge.bookshop.dto.UserTestDto;
import org.teamchallenge.bookshop.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<BookDto> getFavouriteBooks();


    UserDto updateUser(UserDto userDto);
    void deleteUser(Long id);

    UserDto getUserByToken(String jwt);

    User getAuthenticatedUser();

    Optional<User> findUserByEmail(String email);

    void addBookToFavourites(Long id);

    void deleteBookFromFavourites(Long id);
    UserDto findUserById(Long id);

    UserTestDto getUserInfo(String email);

    UserTestDto addUser(UserTestDto userTestDto);
}
