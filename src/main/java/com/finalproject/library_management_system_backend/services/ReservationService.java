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

/**
 * Handles the full lifecycle of book reservations in the library system.
 * <p>
 * Supports creating reservations, collecting books, canceling,
 * returning, and automatically marking overdue reservations.
 */

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final ReservationMapper reservationMapper;

        /**
     * Creates a reservation for a user.
     * <p>
     * Steps performed:
     * <ul>
     *     <li>Validates the user and book</li>
     *     <li>Ensures the book has available copies</li>
     *     <li>Reserves a copy and reduces availability</li>
     *     <li>Sets an expected return date (default: 7 days)</li>
     * </ul>
     *
     * @param request   contains book ID and optional number of days to keep
     * @param userEmail email of the user making the reservation
     * @return the created reservation
     */

    @Transactional
    public ReservationDto createReservation(CreateReservationRequest request, String userEmail) {

        int days = (request.getDaysToKeep() == null || request.getDaysToKeep() <= 0) ? 7 : request.getDaysToKeep();
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

        /**
     * Retrieves every reservation in the system.
     *
     * @return all reservations as DTOs
     */

    @Transactional(readOnly = true)
    public List<ReservationDto> getAllReservations() {
        return reservationRepository.findAll()
                .stream()
                .map(reservationMapper::toDto)
                .collect(Collectors.toList());
    }

        /**
     * Retrieves reservations belonging to a user identified by email.
     *
     * @param userEmail user email
     * @return list of matching reservations
     */

    @Transactional(readOnly = true)
    public List<ReservationDto> getReservationsByUser(String userEmail) {
        return reservationRepository.findByUserEmail(userEmail)
                .stream()
                .map(reservationMapper::toDto)
                .collect(Collectors.toList());
    }

        /**
     * Retrieves reservations belonging to a user identified by ID.
     *
     * @param userId the user’s ID
     * @return list of reservations
     */

    @Transactional(readOnly = true)
    public List<ReservationDto> getReservationsByUserId(Long userId) {
        return reservationRepository.findByUserId(userId)
                .stream()
                .map(reservationMapper::toDto)
                .collect(Collectors.toList());
    }

        /**
     * Marks a reserved book as collected.
     * Reservation must currently be in the RESERVED state.
     *
     * @param reservationId ID of the reservation
     */

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

        /**
     * Cancels a reservation and restores the book’s available copies.
     *
     * @param reservationId reservation ID
     */

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

        /**
     * Runs every day at midnight.
     * <p>
     * Finds all BORROWED reservations whose expected return
     * dates have passed and marks them as OVERDUE.
     */

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

        /**
     * Returns a borrowed or overdue book.
     * <p>
     * Updates status to either RETURNED or LATE_RETURNED,
     * restores available copies, and records the return timestamp.
     *
     * @param reservationId the reservation being completed
     */

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
