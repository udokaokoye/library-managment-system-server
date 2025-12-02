package com.finalproject.library_management_system_backend.controller;

import com.finalproject.library_management_system_backend.dtos.CreateReservationRequest;
import com.finalproject.library_management_system_backend.dtos.ReservationDto;
import com.finalproject.library_management_system_backend.services.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ReservationDto createReservation(@RequestBody CreateReservationRequest request, Principal principal) {
        return reservationService.createReservation(request,principal.getName());
    }

    @GetMapping
    public List<ReservationDto> getAllReservations() {
        return reservationService.getAllReservations();
    }

    @GetMapping("/my-reservations")
    public List<ReservationDto> getMyReservations(Principal principal) {
        return reservationService.getReservationsByUser(principal.getName());
    }

    @GetMapping("/user/{userId}")
    public List<ReservationDto> getUserReservations(@PathVariable Long userId) {
        return reservationService.getReservationsByUserId(userId);

    }

    @PutMapping("/collect/{id}")
    public ResponseEntity<String> collectBook(@PathVariable Long id) {
        reservationService.collectBook(id);
        return ResponseEntity.ok("Book collected (Status: BORROWED).");
    }

    @PutMapping("/cancel/{id}")
    public ResponseEntity<String> cancelReservation(@PathVariable Long id) {
        reservationService.cancelReservation(id);
        return ResponseEntity.ok("Reservation canceled successfully.");
    }

    @PutMapping("/return/{id}")
    public ResponseEntity<String> returnBook(@PathVariable Long id) {
        reservationService.returnBook(id);
        return ResponseEntity.ok("Book returned successfully.");
    }

    @PutMapping("/extend/{id}")
    public ResponseEntity<String> extendReservation(@PathVariable Long id) {
        reservationService.extendReservation(id);
        return ResponseEntity.ok("Loan extended by 7 days.");
    }
}
