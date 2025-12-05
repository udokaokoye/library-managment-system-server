package com.finalproject.library_management_system_backend.controller;


import com.finalproject.library_management_system_backend.dtos.BookDto;
import com.finalproject.library_management_system_backend.dtos.CreateBookRequest;
import com.finalproject.library_management_system_backend.mappers.BookMapper;
import com.finalproject.library_management_system_backend.repositories.BookRepository;
import com.finalproject.library_management_system_backend.services.BookService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * Handles all book-related operations in the library system.
 * <p>
 * Supports listing, retrieving, creating, updating,
 * and deleting books. Most of the core logic is delegated
 * to the BookService.
 */
@AllArgsConstructor
@RestController
@RequestMapping("/books")
public class BookController {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookService bookService;

    /**
     * Returns all books, with optional sorting.
     *
     * @param sort the field to sort by; defaults to title
     * @return a list of books mapped to DTOs
     */
    @GetMapping
    public Iterable<BookDto> getAllBooks(@RequestParam(required = false, defaultValue = "title") String sort) {
        return bookRepository.findAll(Sort.by(sort)).stream().map(book -> bookMapper.toBookDto(book)).toList();
    }

    /**
     * Retrieves a single book by its ID.
     *
     * @param id the ID of the book to look up
     * @return the matching book's details
     */

    @GetMapping("/{id}")
    public BookDto getBookById(@PathVariable Long id) {
        return bookService.getBookById(id);
    }

        /**
     * Creates a new book entry.
     *
     * @param request the book’s details, such as title, author,
     *                genre, and publication info
     * @return the newly created book as a DTO
     */

    @PostMapping
    public BookDto createBook(@RequestBody CreateBookRequest request) {
        return bookService.createBook(request);
    }

        /**
     * Updates an existing book.
     *
     * @param id      the ID of the book to update
     * @param request the updated details
     * @return the updated book as a DTO
     */

    @PutMapping("/{id}")
    public BookDto updateBook(@PathVariable Long id, @RequestBody CreateBookRequest request) {
        return bookService.updateBook(id, request);
    }

        /**
     * Deletes a book by its ID.
     *
     * @param id the ID of the book to remove
     * @return a confirmation message
     */

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok("Book deleted successfully.");
    }
}
