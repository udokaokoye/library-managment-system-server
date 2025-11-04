package com.finalproject.library_management_system_backend.controller;


import com.finalproject.library_management_system_backend.dtos.BookDto;
import com.finalproject.library_management_system_backend.dtos.CreateBookRequest;
import com.finalproject.library_management_system_backend.mappers.BookMapper;
import com.finalproject.library_management_system_backend.repositories.BookRepository;
import com.finalproject.library_management_system_backend.services.BookService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/books")
public class BookController {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookService bookService;


    @GetMapping
    public Iterable<BookDto> getAllBooks(@RequestParam(required = false, defaultValue = "title") String sort) {
        return bookRepository.findAll(Sort.by(sort)).stream().map(book -> bookMapper.toBookDto(book)).toList();
    }

    @PostMapping
    public BookDto createBook(@RequestBody CreateBookRequest request) {
        return bookService.createBook(request);
    }
}
