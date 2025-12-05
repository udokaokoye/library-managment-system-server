package com.finalproject.library_management_system_backend.controller;

import com.finalproject.library_management_system_backend.dtos.DashboardStats;
import com.finalproject.library_management_system_backend.model.ReservationStatus;
import com.finalproject.library_management_system_backend.repositories.BookRepository;
import com.finalproject.library_management_system_backend.repositories.ReservationRepository;
import com.finalproject.library_management_system_backend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;

    @GetMapping("/stats")
    public DashboardStats getDashboardStats() {
        long books = bookRepository.count();
        long users = userRepository.count();

        long totalReservations = reservationRepository.count();

        long active = reservationRepository.findAll().stream()
                .filter(r -> r.getStatus() == ReservationStatus.BORROWED ||
                        r.getStatus() == ReservationStatus.OVERDUE ||
                        r.getStatus() == ReservationStatus.RESERVED)
                .count();

        long overdue = reservationRepository.findAll().stream()
                .filter(r -> r.getStatus() == ReservationStatus.OVERDUE)
                .count();

        return DashboardStats.builder()
                .totalBooks(books)
                .totalUsers(users)
                .activeLoans(active)
                .overdueBooks(overdue)
                .totalReservations(totalReservations)
                .build();
    }
}