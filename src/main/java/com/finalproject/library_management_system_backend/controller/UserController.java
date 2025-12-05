package com.finalproject.library_management_system_backend.controller;

import com.finalproject.library_management_system_backend.dtos.RegisterUserRequest;
import com.finalproject.library_management_system_backend.dtos.UserDto;
import com.finalproject.library_management_system_backend.mappers.UserMapper;
import com.finalproject.library_management_system_backend.repositories.UserRepository;
import com.finalproject.library_management_system_backend.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * Manages actions related to users in the library system.
 * <p>
 * Supports listing users, creating new ones, and retrieving
 * account details for the currently authenticated user.
 */

@AllArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserService userService;

      /**
     * Returns all users, optionally sorted by a provided field.
     *
     * @param sort the field to sort by; defaults to firstName
     * @return a list of users as DTOs
     */

    @GetMapping
    public Iterable<UserDto> getAllUsers(@RequestParam(required = false, defaultValue = "firstName") String sort) {
        return userRepository.findAll(Sort.by(sort)).stream().map(user -> userMapper.toDto(user)).toList();
    }

        /**
     * Creates a new user.
     *
     * @param request basic registration details such as name,
     *                email, and password
     * @return the newly created user as a DTO
     */

    @PostMapping
    public UserDto createUser(@RequestBody RegisterUserRequest request){
        return userService.createUser(request);
    }

        /**
     * Retrieves account details for the currently logged-in user.
     *
     * @param principal provides access to the authenticated user's email
     * @return the user’s profile information
     */

    @GetMapping("/user-details")
    public UserDto getMyUserDetails(Principal principal) {
        return userService.getUserDetailsByEmail(principal.getName());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully.");
    }
}
