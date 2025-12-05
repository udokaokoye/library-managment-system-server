package com.finalproject.library_management_system_backend.services;


import com.finalproject.library_management_system_backend.dtos.RegisterUserRequest;
import com.finalproject.library_management_system_backend.dtos.UserDto;
import com.finalproject.library_management_system_backend.mappers.UserMapper;
import com.finalproject.library_management_system_backend.repositories.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
/**
 * Handles user-related operations such as retrieving user details
 * and creating new users in the system.
 */
@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final EntityManager entityManager;

        /**
     * Retrieves user information based on email.
     *
     * @param email the email address of the user
     * @return a UserDto containing the user’s details
     * @throws java.util.NoSuchElementException if the user is not found
     */

    public UserDto getUserDetailsByEmail(String email){
        var userEntity = userRepository.findByEmail(email).orElseThrow();
        return userMapper.toDto(userEntity);
    }

        /**
     * Creates a new user record in the database.
     *
     * @param request the payload containing user registration details
     * @return the created user as a UserDto
     */

    @Transactional
    public UserDto createUser(@RequestBody RegisterUserRequest request){
        var userEntity = userMapper.toEntity(request);
        entityManager.persist(userEntity);
        return userMapper.toDto(userEntity);
 }


}
