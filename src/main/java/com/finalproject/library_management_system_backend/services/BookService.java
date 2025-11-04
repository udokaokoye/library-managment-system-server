package com.finalproject.library_management_system_backend.services;


import com.finalproject.library_management_system_backend.dtos.BookDto;
import com.finalproject.library_management_system_backend.dtos.CreateBookRequest;
import com.finalproject.library_management_system_backend.mappers.BookMapper;
import com.finalproject.library_management_system_backend.repositories.BookRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@AllArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final EntityManager entityManager;

    @Transactional
    public BookDto createBook(@RequestBody CreateBookRequest request) {
        var bookEntity = bookMapper.toEntity(request);
        entityManager.persist(bookEntity);
        return bookMapper.toBookDto(bookEntity);
    }

}
