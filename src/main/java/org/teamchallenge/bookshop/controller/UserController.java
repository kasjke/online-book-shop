package org.teamchallenge.bookshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.teamchallenge.bookshop.dto.BookDto;
import org.teamchallenge.bookshop.dto.UserDto;
import org.teamchallenge.bookshop.dto.UserTestDto;
import org.teamchallenge.bookshop.service.PasswordResetService;
import org.teamchallenge.bookshop.service.UserService;

import java.util.List;

import static org.teamchallenge.bookshop.constants.ValidationConstants.PASSWORD_SAVED;

@RestController
@RequestMapping("api/v1/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    private final PasswordResetService passwordResetService;

    @Operation(summary = "Add a book to user's favourites")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Book added to favourites"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping("/favourites/add")
    public ResponseEntity<Void> addBookToFavourites(@RequestParam Long id) {
        userService.addBookToFavourites(id);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @Operation(summary = "Delete a book from user's favourites")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Book deleted from favourites"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping("/favourites/delete")
    public ResponseEntity<Void> deleteBookFromFavourites(@RequestParam Long id) {
        userService.deleteBookFromFavourites(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Get user's favourite books")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fetched user's favourite books", content = @Content(mediaType = "application/json", schema = @Schema(implementation = BookDto.class))),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/favourites")
    public ResponseEntity<List<BookDto>> getUserFavourites() {
        return ResponseEntity.ok(userService.getFavouriteBooks());
    }

    @Operation(summary = "Initiate password reset")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset link sent"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping("/resetPassword")
    public ResponseEntity<String> resetPassword(@RequestParam String userEmail) {
        passwordResetService.initiatePasswordReset(userEmail);
        return ResponseEntity.ok("Password reset link sent to your email");
    }

    @Operation(summary = "Save a new password using reset token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password saved"),
            @ApiResponse(responseCode = "400", description = "Invalid token or password")
    })
    @PostMapping("/savePassword")
    public ResponseEntity<String> savePassword(@RequestParam String token, @RequestParam String newPassword) {
        passwordResetService.saveNewPassword(token, newPassword);
        return ResponseEntity.ok(PASSWORD_SAVED);
    }

    @Operation(summary = "Update user details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User details updated", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/update")
    private ResponseEntity<UserDto> userUpdate(@RequestBody UserDto userDto) {
        UserDto updatedUserDto = userService.updateUser(userDto);
        return ResponseEntity.ok(updatedUserDto);
    }

    @Operation(summary = "Find user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/findById/{id}")
    public ResponseEntity<UserDto> findById(@Valid @PathVariable Long id) {
        UserDto userDto = userService.findUserById(id);
        return ResponseEntity.ok(userDto);
    }

    @Operation(summary = "Delete user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUserById(@Valid @PathVariable Long id) {
        userService.deleteUser(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Get user data by token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User data retrieved", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/userByToken")
    public ResponseEntity<UserDto> getUserDataByToken(@RequestParam String token) {
        UserDto userDto = userService.getUserByToken(token);
        return ResponseEntity.ok(userDto);
    }

    @Operation(summary = "Add a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User added", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserTestDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/add")
    public ResponseEntity<UserTestDto> addUser(@RequestBody UserTestDto userTestDto) {
        return ResponseEntity.ok(userService.addUser(userTestDto));
    }

    @Operation(summary = "Get user info by email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User info retrieved", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/info")
    public ResponseEntity<UserTestDto> userInfo(@RequestParam String email) {
        return ResponseEntity.ok(userService.getUserInfo(email));
    }

}
