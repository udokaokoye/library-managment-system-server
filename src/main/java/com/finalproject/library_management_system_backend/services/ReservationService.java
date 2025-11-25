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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final ReservationMapper reservationMapper;

    private static final int DEFAULT_DAYS_TO_KEEP = 7;
    @Transactional
    public ReservationDto createReservation(CreateReservationRequest request, String userEmail) {

        int days = (request.getDaysToKeep() == null || request.getDaysToKeep() <= 0) ? DEFAULT_DAYS_TO_KEEP : request.getDaysToKeep();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expectedReturn = now.plusDays(days);

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
                .expectedReturnDate(expectedReturn)
                .status(ReservationStatus.RESERVED)
                .build();

        Reservation savedReservation = reservationRepository.save(reservation);

        return reservationMapper.toDto(savedReservation);
    }

    @Transactional(readOnly = true)
    public List<ReservationDto> getAllReservations() {
        return reservationRepository.findAll()
                .stream()
                .map(reservationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReservationDto> getReservationsByUser(String userEmail) {
        return reservationRepository.findByUserEmail(userEmail)
                .stream()
                .map(reservationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReservationDto> getReservationsByUserId(Long userId) {
        return reservationRepository.findByUserId(userId)
                .stream()
                .map(reservationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void collectBook(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        if (reservation.getStatus() != ReservationStatus.RESERVED) {
            throw new RuntimeException("Cannot collect. Reservation is not RESERVED.");
        }

        reservation.setStatus(ReservationStatus.BORROWED);
        reservationRepository.save(reservation);

    }

    @Transactional
    public void cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        if (reservation.getStatus() != ReservationStatus.RESERVED) {
            throw new RuntimeException("Cannot cancel. Status is not RESERVED.");
        }

        reservation.setStatus(ReservationStatus.CANCELED);
        reservationRepository.save(reservation);

        Book book = reservation.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);
    }

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void checkOverdueReservations() {
        LocalDateTime now = LocalDateTime.now();

        // Find books that are still 'BORROWED' but past their due date
        List<Reservation> overdueList = reservationRepository.findByStatusAndExpectedReturnDateBefore(
                ReservationStatus.BORROWED, now
        );

        for (Reservation r : overdueList) {
            r.setStatus(ReservationStatus.OVERDUE);
        }

        if (!overdueList.isEmpty()) {
            reservationRepository.saveAll(overdueList);
            System.out.println("Marked " + overdueList.size() + " books as OVERDUE.");
        }
    }

    @Transactional
    public void returnBook(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        if (reservation.getStatus() != ReservationStatus.BORROWED &&
                reservation.getStatus() != ReservationStatus.OVERDUE) {
            throw new RuntimeException("Cannot return. Book is not currently active (Borrowed or Overdue).");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expectedReturn = reservation.getExpectedReturnDate();

        if (now.isAfter(expectedReturn)) {
            reservation.setStatus(ReservationStatus.LATE_RETURNED);
        } else {
            reservation.setStatus(ReservationStatus.RETURNED);
        }

        reservation.setReturnDate(now);
        reservationRepository.save(reservation);

        Book book = reservation.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);
    }
}
