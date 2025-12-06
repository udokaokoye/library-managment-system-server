package com.finalproject.library_management_system_backend.services;


import com.finalproject.library_management_system_backend.dtos.BookDto;
import com.finalproject.library_management_system_backend.dtos.CreateBookRequest;
import com.finalproject.library_management_system_backend.mappers.BookMapper;
import com.finalproject.library_management_system_backend.model.Book;
import com.finalproject.library_management_system_backend.repositories.BookRepository;
import com.finalproject.library_management_system_backend.repositories.ReservationRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;

/**
 * Handles business logic for creating, retrieving, updating,
 * and deleting books in the library system.
 * <p>
 * Validates copy counts, manages availability updates, and ensures
 * reservations linked to a book are removed before deletion.
 */

@Service
@AllArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final ReservationRepository reservationRepository;
    private final EntityManager entityManager;
    /**
     * Creates a new book in the system.
     * <p>
     * Automatically sets {@code availableCopies} equal to {@code totalCopies}.
     *
     * @param request details such as title, author, publication year,
     *                picture URL, and total number of copies
     * @return the created book as a DTO
     */
    @Transactional
    public BookDto createBook(@RequestBody CreateBookRequest request) {

        var bookEntity = bookMapper.toEntity(request);
        bookEntity.setAvailableCopies(bookEntity.getTotalCopies());
        Book savedBook = bookRepository.save(bookEntity);
        return bookMapper.toBookDto(savedBook);
    }

        /**
     * Retrieves a book by its ID.
     *
     * @param id the ID of the book to look up
     * @return the book as a DTO
     * @throws ResponseStatusException if the book does not exist
     */

    @Transactional(readOnly = true)
    public BookDto getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
        return bookMapper.toBookDto(book);
    }

        /**
     * Updates an existing book and recalculates available copies.
     * <p>
     * Prevents reducing the total number of copies below the number
     * currently borrowed.
     *
     * @param id      the ID of the book to update
     * @param request the updated book data
     * @return the updated book as a DTO
     * @throws EntityNotFoundException if the book does not exist
     * @throws RuntimeException if the update would produce invalid copy counts
     */

    @Transactional
    public BookDto updateBook(Long id, CreateBookRequest request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));

        int oldTotal = book.getTotalCopies();
        int newTotal = request.getTotalCopies();
        int difference = newTotal - oldTotal;

        int newAvailable = book.getAvailableCopies() + difference;

        if (newAvailable < 0) {
            throw new RuntimeException("Cannot reduce total copies. " + Math.abs(newAvailable) + " copies are currently borrowed and would be unaccounted for.");
        }


        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setPublicationYear(request.getPublicationYear());
        book.setPictureUrl(request.getPictureUrl());
        book.setTotalCopies(newTotal);
        book.setAvailableCopies(newAvailable);

        Book savedBook = bookRepository.save(book);
        return bookMapper.toBookDto(savedBook);
    }

        /**
     * Deletes a book from the system.
     * <p>
     * Any reservations for this book are deleted first to avoid
     * orphaned records.
     *
     * @param id the ID of the book to delete
     * @throws ResponseStatusException if the book does not exist
     */

    @Transactional
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found");
        }

        var reservations = reservationRepository.findAll().stream()
                .filter(r -> r.getBook().getId().equals(id))
                .toList();

        if (!reservations.isEmpty()) {
            reservationRepository.deleteAll(reservations);
        }

        bookRepository.deleteById(id);
    }
}
