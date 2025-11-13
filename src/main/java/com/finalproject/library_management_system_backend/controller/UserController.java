package com.finalproject.library_management_system_backend.controller;

import com.finalproject.library_management_system_backend.dtos.RegisterUserRequest;
import com.finalproject.library_management_system_backend.dtos.UserDto;
import com.finalproject.library_management_system_backend.mappers.UserMapper;
import com.finalproject.library_management_system_backend.repositories.UserRepository;
import com.finalproject.library_management_system_backend.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@AllArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserService userService;

    @GetMapping
    public Iterable<UserDto> getAllUsers(@RequestParam(required = false, defaultValue = "firstName") String sort) {
        return userRepository.findAll(Sort.by(sort)).stream().map(user -> userMapper.toDto(user)).toList();
    }

    @PostMapping
    public UserDto createUser(@RequestBody RegisterUserRequest request){
        return userService.registerUser(request);
    }

    @GetMapping("/user-details")
    public UserDto getMyUserDetails(Principal principal) {
        return userService.getUserDetailsByEmail(principal.getName());
    }
}
