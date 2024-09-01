package org.teamchallenge.bookshop.service.Impl;


import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.teamchallenge.bookshop.dto.BookDto;
import org.teamchallenge.bookshop.dto.UserDto;
import org.teamchallenge.bookshop.dto.UserTestDto;
import org.teamchallenge.bookshop.exception.UserAlreadyExistsException;
import org.teamchallenge.bookshop.exception.UserNotFoundException;
import org.teamchallenge.bookshop.mapper.BookMapper;
import org.teamchallenge.bookshop.mapper.UserMapper;
import org.teamchallenge.bookshop.model.Book;
import org.teamchallenge.bookshop.model.Cart;
import org.teamchallenge.bookshop.model.User;
import org.teamchallenge.bookshop.repository.BookRepository;
import org.teamchallenge.bookshop.repository.TokenRepository;
import org.teamchallenge.bookshop.repository.UserRepository;
import org.teamchallenge.bookshop.secutity.JwtService;
import org.teamchallenge.bookshop.service.UserService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final BookMapper bookMapper;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final BookRepository bookRepository;

    @Override
    public List<BookDto> getFavouriteBooks() {
        User user = getAuthenticatedUser();
        return userRepository.findFavouritesById(userRepository.findIdByEmail(user.getEmail()).get())
                .stream()
                .map(bookMapper::entityToDTO)
                .toList();
    }


    @Override
    @Transactional
    public UserDto updateUser(UserDto userDto) {
        User existingUser = userRepository.findById(userDto.id())
                .orElseThrow(UserNotFoundException::new);

        userMapper.updateUserFromDto(userDto, existingUser);
        User updatedUser = userRepository.save(existingUser);

        return userMapper.entityToDto(updatedUser);
    }

    @Transactional
    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        tokenRepository.deleteAllByUser(user);
        userRepository.deleteById(id);
    }


    @Override
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void addBookToFavourites(Long id) {
        User user = getAuthenticatedUser();
        List<Book> list = user.getFavourites();
        Book book = bookRepository.findById(id).get();
        if (!list.contains(book)) {
            list.add(book);
        }
        user.setFavourites(list);
        userRepository.save(user);
    }


    @Override
    public void deleteBookFromFavourites(Long id) {
        User user = getAuthenticatedUser();
        List<Book> list = user.getFavourites();
        list.removeIf(x -> x.getId() == id);
        user.setFavourites(list);
        userRepository.save(user);
    }

    public UserDto getUserByToken(String jwt) {
        String username = String.valueOf(jwtService.extractUserId(jwt));
        User user = userRepository.findById(Long.valueOf(username))
                .orElseThrow(UserNotFoundException::new);
        return userMapper.entityToDto(user);
    }

    @Override
    public UserDto findUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        return userMapper.entityToDto(user);
    }

    @Override
    public UserTestDto getUserInfo(String email) {
        User user = userRepository.findByEmail(email).get();
        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public UserTestDto addUser(UserTestDto userTestDto) {
        if (userRepository.existsByEmail(userTestDto.getEmail())) {
            throw new UserAlreadyExistsException();
        }
        User user = userMapper.toEntity(userTestDto);
        user.setPassword(passwordEncoder.encode(userTestDto.getPassword()));

        Cart cart = new Cart();
        cart.setIsPermanent(true);
        cart.setLastModified(LocalDate.now());
        user.setCart(cart);

        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        return userRepository.findById(Long.valueOf(userId)).get();
    }
}
