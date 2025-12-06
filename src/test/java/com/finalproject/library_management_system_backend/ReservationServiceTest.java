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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ReservationMapper reservationMapper;

    @InjectMocks
    private ReservationService reservationService;

    @Test
    void createReservation_withAvailableBook_savesReservationAndDecrementsCopies() {
        // arrange
        String userEmail = "test@example.com";
        Long bookId = 1L;

        CreateReservationRequest request = new CreateReservationRequest();
        request.setBookId(bookId);
        request.setDaysToKeep(7);

        User user = User.builder()
                .id(10L)
                .email(userEmail)
                .build();

        Book book = Book.builder()
                .id(bookId)
                .title("Clean Code")
                .availableCopies(3)
                .totalCopies(3)
                .build();

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        // make save() return the same reservation instance that was passed in
        when(reservationRepository.save(any(Reservation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ReservationDto expectedDto = new ReservationDto();
        when(reservationMapper.toDto(any(Reservation.class))).thenReturn(expectedDto);

        // act
        ReservationDto result = reservationService.createReservation(request, userEmail);

        // assert
        assertSame(expectedDto, result);

        // verify book copies were decremented and saved
        assertEquals(2, book.getAvailableCopies());
        verify(bookRepository).save(book);

        // capture the reservation that was saved
        ArgumentCaptor<Reservation> captor = ArgumentCaptor.forClass(Reservation.class);
        verify(reservationRepository).save(captor.capture());
        Reservation saved = captor.getValue();

        assertEquals(user, saved.getUser());
        assertEquals(book, saved.getBook());
        assertEquals(ReservationStatus.RESERVED, saved.getStatus());
        assertNotNull(saved.getExpectedReturnDate());
        assertNotNull(saved.getReservationDate());
        assertTrue(saved.getExpectedReturnDate().isAfter(saved.getReservationDate()));
    }

    @Test
    void createReservation_whenNoAvailableCopies_throwsException() {
        // arrange
        String userEmail = "test@example.com";
        Long bookId = 1L;

        CreateReservationRequest request = new CreateReservationRequest();
        request.setBookId(bookId);
        request.setDaysToKeep(7);

        User user = User.builder()
                .id(10L)
                .email(userEmail)
                .build();

        Book book = Book.builder()
                .id(bookId)
                .title("Clean Code")
                .availableCopies(0)
                .totalCopies(3)
                .build();

        when(userRepository.findByEmail(userEmail)).thenReturn(Optional.of(user));
        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        // act + assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> reservationService.createReservation(request, userEmail));

        assertEquals("No available copies for this book", ex.getMessage());
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void getAllReservations_mapsEntitiesToDtos() {
        // arrange
        Reservation r1 = Reservation.builder().id(1L).build();
        Reservation r2 = Reservation.builder().id(2L).build();

        when(reservationRepository.findAll()).thenReturn(List.of(r1, r2));
        when(reservationMapper.toDto(any(Reservation.class)))
                .thenReturn(new ReservationDto(), new ReservationDto());

        // act
        List<ReservationDto> result = reservationService.getAllReservations();

        // assert
        assertEquals(2, result.size());
        verify(reservationRepository).findAll();
        verify(reservationMapper, times(2)).toDto(any(Reservation.class));
    }

    @Test
    void returnBook_whenOverdue_setsLateReturnedAndIncrementsCopies() {
        // arrange
        Long reservationId = 1L;

        Book book = Book.builder()
                .id(5L)
                .title("Clean Code")
                .availableCopies(1)
                .build();

        LocalDateTime pastDue = LocalDateTime.now().minusDays(1);

        Reservation reservation = Reservation.builder()
                .id(reservationId)
                .book(book)
                .status(ReservationStatus.BORROWED)
                .expectedReturnDate(pastDue)
                .build();

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

        // act
        reservationService.returnBook(reservationId);

        // assert
        assertEquals(ReservationStatus.LATE_RETURNED, reservation.getStatus());
        assertNotNull(reservation.getReturnDate());
        assertEquals(2, book.getAvailableCopies());

        verify(reservationRepository).save(reservation);
        verify(bookRepository).save(book);
    }

    @Test
    void extendReservation_fromOverdueToBorrowed_whenNewDueAfterNow_updatesStatus() {
        // arrange
        Long reservationId = 1L;

        LocalDateTime oldDueDate = LocalDateTime.now().minusDays(1); // overdue
        Reservation reservation = Reservation.builder()
                .id(reservationId)
                .status(ReservationStatus.OVERDUE)
                .expectedReturnDate(oldDueDate)
                .build();

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

        // act
        reservationService.extendReservation(reservationId);

        // assert
        assertNotNull(reservation.getExpectedReturnDate());
        assertTrue(reservation.getExpectedReturnDate().isAfter(oldDueDate));
        assertEquals(ReservationStatus.BORROWED, reservation.getStatus());
        verify(reservationRepository).save(reservation);
    }

    @Test
    void collectBook_whenStatusReserved_setsBorrowed() {
        // arrange
        Long reservationId = 1L;
        Reservation reservation = Reservation.builder()
                .id(reservationId)
                .status(ReservationStatus.RESERVED)
                .build();

        when(reservationRepository.findById(reservationId)).thenReturn(Optional.of(reservation));

        // act
        reservationService.collectBook(reservationId);

        // assert
        assertEquals(ReservationStatus.BORROWED, reservation.getStatus());
        verify(reservationRepository).save(reservation);
    }
}
