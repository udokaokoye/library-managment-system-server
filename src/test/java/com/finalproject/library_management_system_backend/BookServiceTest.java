package com.finalproject.library_management_system_backend.services;

import com.finalproject.library_management_system_backend.dtos.BookDto;
import com.finalproject.library_management_system_backend.dtos.CreateBookRequest;
import com.finalproject.library_management_system_backend.mappers.BookMapper;
import com.finalproject.library_management_system_backend.model.Book;
import com.finalproject.library_management_system_backend.model.Reservation;
import com.finalproject.library_management_system_backend.repositories.BookRepository;
import com.finalproject.library_management_system_backend.repositories.ReservationRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private BookService bookService;

    @Test
    void createBook_setsAvailableCopiesAndReturnsDto() {
        // arrange
        CreateBookRequest request = new CreateBookRequest();
        request.setTitle("Clean Code");
        request.setAuthor("Robert C. Martin");
        request.setPublicationYear(2008);
        request.setPictureUrl("http://example.com/clean-code.jpg");
        request.setTotalCopies(5);

        Book bookEntity = new Book();
        bookEntity.setTotalCopies(5);

        when(bookMapper.toEntity(request)).thenReturn(bookEntity);

        Book savedBook = new Book();
        savedBook.setId(1L);
        savedBook.setTitle("Clean Code");
        savedBook.setTotalCopies(5);
        savedBook.setAvailableCopies(5);

        when(bookRepository.save(bookEntity)).thenReturn(savedBook);

        BookDto dto = new BookDto();
        when(bookMapper.toBookDto(savedBook)).thenReturn(dto);

        // act
        BookDto result = bookService.createBook(request);

        // assert
        assertSame(dto, result);
        assertEquals(5, bookEntity.getAvailableCopies());
        verify(bookRepository).save(bookEntity);
        verify(bookMapper).toEntity(request);
        verify(bookMapper).toBookDto(savedBook);
    }

    @Test
    void getBookById_whenBookExists_returnsDto() {
        // arrange
        Long id = 1L;
        Book book = new Book();
        book.setId(id);

        when(bookRepository.findById(id)).thenReturn(Optional.of(book));

        BookDto dto = new BookDto();
        when(bookMapper.toBookDto(book)).thenReturn(dto);

        // act
        BookDto result = bookService.getBookById(id);

        // assert
        assertSame(dto, result);
        verify(bookRepository).findById(id);
        verify(bookMapper).toBookDto(book);
    }

    @Test
    void getBookById_whenBookDoesNotExist_throwsResponseStatusException() {
        // arrange
        Long id = 1L;
        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        // act + assert
        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> bookService.getBookById(id)
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("Book not found", ex.getReason());
    }

    @Test
    void updateBook_withValidCopyChange_updatesFieldsAndSaves() {
        // arrange
        Long id = 1L;

        Book existing = new Book();
        existing.setId(id);
        existing.setTitle("Old Title");
        existing.setAuthor("Old Author");
        existing.setPublicationYear(2000);
        existing.setPictureUrl("old-url");
        existing.setTotalCopies(5);
        existing.setAvailableCopies(3);

        when(bookRepository.findById(id)).thenReturn(Optional.of(existing));

        CreateBookRequest request = new CreateBookRequest();
        request.setTitle("New Title");
        request.setAuthor("New Author");
        request.setPublicationYear(2020);
        request.setPictureUrl("new-url");
        request.setTotalCopies(7); // +2 total copies

        Book savedBook = existing; // same instance after mutation
        when(bookRepository.save(existing)).thenReturn(savedBook);

        BookDto dto = new BookDto();
        when(bookMapper.toBookDto(savedBook)).thenReturn(dto);

        // act
        BookDto result = bookService.updateBook(id, request);

        // assert
        assertSame(dto, result);
        assertEquals("New Title", existing.getTitle());
        assertEquals("New Author", existing.getAuthor());
        assertEquals(2020, existing.getPublicationYear());
        assertEquals("new-url", existing.getPictureUrl());
        assertEquals(7, existing.getTotalCopies());
        assertEquals(5, existing.getAvailableCopies()); // 3 + (7 - 5) = 5

        verify(bookRepository).save(existing);
        verify(bookMapper).toBookDto(savedBook);
    }

    @Test
    void updateBook_whenNewAvailableWouldBeNegative_throwsRuntimeException() {
        // arrange
        Long id = 1L;

        Book existing = new Book();
        existing.setId(id);
        existing.setTotalCopies(5);
        existing.setAvailableCopies(0); // everything borrowed

        when(bookRepository.findById(id)).thenReturn(Optional.of(existing));

        CreateBookRequest request = new CreateBookRequest();
        request.setTotalCopies(3); // reduce by 2 -> newAvailable = -2

        // act + assert
        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> bookService.updateBook(id, request)
        );

        assertTrue(ex.getMessage().startsWith("Cannot reduce total copies."));
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void updateBook_whenBookNotFound_throwsEntityNotFoundException() {
        // arrange
        Long id = 1L;
        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        CreateBookRequest request = new CreateBookRequest();
        request.setTotalCopies(5);

        // act + assert
        assertThrows(EntityNotFoundException.class,
                () -> bookService.updateBook(id, request));
    }

    @Test
    void deleteBook_whenBookDoesNotExist_throwsResponseStatusException() {
        // arrange
        Long id = 1L;
        when(bookRepository.existsById(id)).thenReturn(false);

        // act + assert
        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> bookService.deleteBook(id)
        );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertEquals("Book not found", ex.getReason());

        verify(reservationRepository, never()).deleteAll(anyList());
        verify(bookRepository, never()).deleteById(anyLong());
    }

    @Test
    void deleteBook_withReservations_deletesReservationsThenBook() {
        // arrange
        Long id = 1L;
        when(bookRepository.existsById(id)).thenReturn(true);

        // mock reservations pointing to this book
        Book book = new Book();
        book.setId(id);

        Reservation r1 = mock(Reservation.class);
        Reservation r2 = mock(Reservation.class);

        when(r1.getBook()).thenReturn(book);
        when(r2.getBook()).thenReturn(book);

        when(reservationRepository.findAll()).thenReturn(List.of(r1, r2));

        // act
        bookService.deleteBook(id);

        // assert
        verify(reservationRepository).deleteAll(anyList());
        verify(bookRepository).deleteById(id);
    }

    @Test
    void deleteBook_withoutReservations_deletesBookOnly() {
        // arrange
        Long id = 1L;
        when(bookRepository.existsById(id)).thenReturn(true);
        when(reservationRepository.findAll()).thenReturn(Collections.emptyList());

        // act
        bookService.deleteBook(id);

        // assert
        verify(reservationRepository, never()).deleteAll(anyList());
        verify(bookRepository).deleteById(id);
    }
}
