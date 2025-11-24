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

@Service
@AllArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final ReservationRepository reservationRepository;
    private final EntityManager entityManager;

    @Transactional
    public BookDto createBook(@RequestBody CreateBookRequest request) {
        var bookEntity = bookMapper.toEntity(request);
        entityManager.persist(bookEntity);
        return bookMapper.toBookDto(bookEntity);
    }

    @Transactional(readOnly = true) // Optimization: Tells DB we won't modify data
    public BookDto getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));
        return bookMapper.toBookDto(book);
    }

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

        // Update fields
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setPublicationYear(request.getPublicationYear());
        book.setPictureUrl(request.getPictureUrl());
        book.setTotalCopies(newTotal);
        book.setAvailableCopies(newAvailable);

        Book savedBook = bookRepository.save(book);
        return bookMapper.toBookDto(savedBook);
    }

    @Transactional
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new EntityNotFoundException("Book not found");
        }

        if (reservationRepository.existsByBookId(id)) {
            throw new RuntimeException("Cannot delete this book because it has associated reservations. Consider archiving it or deleting the reservations first.");
        }

        bookRepository.deleteById(id);
    }
}
