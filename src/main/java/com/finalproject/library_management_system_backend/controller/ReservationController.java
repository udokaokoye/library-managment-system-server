package com.finalproject.library_management_system_backend.controller;

import com.finalproject.library_management_system_backend.dtos.CreateReservationRequest;
import com.finalproject.library_management_system_backend.dtos.ReservationDto;
import com.finalproject.library_management_system_backend.services.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 * Handles reservation-related actions in the library system.
 * <p>
 * Users can create reservations, view their own reservations,
 * and perform status updates like collecting, canceling,
 * or returning reserved books.
 */

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

       /**
     * Creates a reservation for the authenticated user.
     *
     * @param request   details about the book being reserved
     * @param principal provides access to the current user's identity
     * @return the newly created reservation
     */

    @PostMapping
    public ReservationDto createReservation(@RequestBody CreateReservationRequest request, Principal principal) {
        return reservationService.createReservation(request,principal.getName());
    }

        /**
     * Retrieves all reservations in the system.
     * Intended for admin or staff use.
     *
     * @return a list of all reservations
     */

    @GetMapping
    public List<ReservationDto> getAllReservations() {
        return reservationService.getAllReservations();
    }

        /**
     * Retrieves reservations belonging to the current user.
     *
     * @param principal identifies the logged-in user
     * @return the user's reservations
     */

    @GetMapping("/my-reservations")
    public List<ReservationDto> getMyReservations(Principal principal) {
        return reservationService.getReservationsByUser(principal.getName());
    }

        /**
     * Retrieves reservations for a specific user by ID.
     *
     * @param userId the user whose reservations should be returned
     * @return a list of reservations for that user
     */

    @GetMapping("/user/{userId}")
    public List<ReservationDto> getUserReservations(@PathVariable Long userId) {
        return reservationService.getReservationsByUserId(userId);

    }

        /**
     * Marks a reserved book as collected.
     *
     * @param id the reservation ID to update
     * @return a confirmation message
     */

    @PutMapping("/collect/{id}")
    public ResponseEntity<String> collectBook(@PathVariable Long id) {
        reservationService.collectBook(id);
        return ResponseEntity.ok("Book collected (Status: BORROWED).");
    }

        /**
     * Cancels a reservation.
     *
     * @param id the reservation ID to cancel
     * @return a confirmation message
     */

    @PutMapping("/cancel/{id}")
    public ResponseEntity<String> cancelReservation(@PathVariable Long id) {
        reservationService.cancelReservation(id);
        return ResponseEntity.ok("Reservation canceled successfully.");
    }

        /**
     * Marks a reserved book as returned.
     *
     * @param id the reservation ID to update
     * @return a confirmation message
     */

    @PutMapping("/return/{id}")
    public ResponseEntity<String> returnBook(@PathVariable Long id) {
        reservationService.returnBook(id);
        return ResponseEntity.ok("Book returned successfully.");
    }
}
