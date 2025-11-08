package com.finalproject.library_management_system_backend.services;

import com.finalproject.library_management_system_backend.dtos.CreateReservationRequest;
import com.finalproject.library_management_system_backend.dtos.ReservationDto;
import com.finalproject.library_management_system_backend.mappers.ReservationMapper;
import com.finalproject.library_management_system_backend.model.Book;
import com.finalproject.library_management_system_backend.model.Reservation;
import com.finalproject.library_management_system_backend.model.ReservationStatus;
import com.finalproject.library_management_system_backend.model.User;
import com.finalproject.library_management_system_backend.repositories.BookRepository;
import com.finalproject.library_management_system_backend.repositories.ReservationRepository;
import com.finalproject.library_management_system_backend.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final ReservationMapper reservationMapper;

    @Transactional
    public ReservationDto createReservation(CreateReservationRequest request, String userEmail) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (book.getAvailableCopies() <= 0) {
            throw new RuntimeException("No available copies for this book");
        }

        book.setAvailableCopies(book.getAvailableCopies()-1);
        bookRepository.save(book);

        Reservation reservation = Reservation.builder()
                .user(user)
                .book(book)
                .reservationDate(LocalDateTime.now())
                .status(ReservationStatus.BORROWED)
                .build();

        Reservation savedReservation = reservationRepository.save(reservation);

        return reservationMapper.toDto(savedReservation);
    }

}
