package com.finalproject.library_management_system_backend.controller;

import com.finalproject.library_management_system_backend.dtos.CreateReservationRequest;
import com.finalproject.library_management_system_backend.dtos.ReservationDto;
import com.finalproject.library_management_system_backend.services.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    public ReservationDto createReservation(@RequestBody CreateReservationRequest request, Principal principal) {
        return reservationService.createReservation(request,principal.getName());
    }
}
