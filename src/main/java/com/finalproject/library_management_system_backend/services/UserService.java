package com.finalproject.library_management_system_backend.services;


import com.finalproject.library_management_system_backend.dtos.RegisterUserRequest;
import com.finalproject.library_management_system_backend.dtos.UserDto;
import com.finalproject.library_management_system_backend.mappers.UserMapper;
import com.finalproject.library_management_system_backend.model.User;
import com.finalproject.library_management_system_backend.repositories.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import com.finalproject.library_management_system_backend.model.Reservation;
import com.finalproject.library_management_system_backend.repositories.ReservationRepository;

import java.util.List;

/**
 * Handles operations related to user accounts, including creation,
 * retrieval, and deletion. Ensures related reservations are handled
 * consistently when accounts are removed.
 */

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final ReservationRepository reservationRepository;
    private final EntityManager entityManager;


        /**
     * Retrieves user details based on the provided email address.
     *
     * @param email the user’s email
     * @return a {@link UserDto} containing the user’s information
     * @throws java.util.NoSuchElementException if no user is found
     */
    public UserDto getUserDetailsByEmail(String email){
        var userEntity = userRepository.findByEmail(email).orElseThrow();
        return userMapper.toDto(userEntity);
    }


        /**
     * Creates a new user and persists it to the database.
     *
     * @param request contains registration details such as
     *                first name, last name, email, and user type
     * @return the created user as a DTO
     */
    @Transactional
    public UserDto createUser(@RequestBody RegisterUserRequest request){
        var userEntity = userMapper.toEntity(request);
        entityManager.persist(userEntity);
        return userMapper.toDto(userEntity);
 }


        /**
     * Deletes a user and any reservations associated with them.
     * <p>
     * Additional rules:
     * <ul>
     *     <li>Administrator accounts cannot be deleted</li>
     *     <li>All of the user’s reservations are removed before deleting the account</li>
     * </ul>
     *
     * @param userId the ID of the user to delete
     * @throws RuntimeException if the user does not exist or is an administrator
     */
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if ("ADMIN".equalsIgnoreCase(user.getUserType().getTypeName())) {
            throw new RuntimeException("Cannot delete an Administrator account.");
        }

        List<Reservation> userReservations = reservationRepository.findByUserId(userId);
        if (!userReservations.isEmpty()) {
            reservationRepository.deleteAll(userReservations);
        }

        userRepository.deleteById(userId);
    }

}
