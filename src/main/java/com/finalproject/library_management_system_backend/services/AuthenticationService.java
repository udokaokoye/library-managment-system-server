package com.finalproject.library_management_system_backend.services;

import com.finalproject.library_management_system_backend.dtos.RegisterUserRequest;
import com.finalproject.library_management_system_backend.dtos.UserDto;
import com.finalproject.library_management_system_backend.mappers.UserMapper;
import com.finalproject.library_management_system_backend.model.User;
import com.finalproject.library_management_system_backend.model.UserType;
import com.finalproject.library_management_system_backend.repositories.UserRepository;
import com.finalproject.library_management_system_backend.repositories.UserTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Handles authentication-related logic, such as registering new users.
 * <p>
 * Performs validation checks, assigns user types, encodes passwords,
 * and persists new accounts to the database.
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final UserTypeRepository userTypeRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

        /**
     * Registers a new user in the system.
     * <p>
     * Steps performed:
     * <ul>
     *     <li>Checks whether the email already exists</li>
     *     <li>Fetches and assigns the correct user type</li>
     *     <li>Encodes the user’s password</li>
     *     <li>Saves the new user and returns a DTO representation</li>
     * </ul>
     *
     * @param request the incoming registration details such as
     *                first name, last name, email, password,
     *                and user type ID
     * @return a {@link UserDto} describing the created user
     * @throws ResponseStatusException if an account already exists
     *                                 with the provided email
     */

    public UserDto register(RegisterUserRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            System.out.println("--- DUPLICATE USER FOUND! ---");

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "An account with this email already exists."
            );
        }

        System.out.println("--- NO DUPLICATE. CREATING NEW USER... ---");
        UserType userType = userTypeRepository.findById(request.getUserTypeId())
                .orElseThrow(() -> new RuntimeException("User type not found"));

        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .userType(userType)
                .build();

        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }
}
