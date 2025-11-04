package com.finalproject.library_management_system_backend.mappers;

import com.finalproject.library_management_system_backend.dtos.BookDto;
import com.finalproject.library_management_system_backend.dtos.CreateBookRequest;
import com.finalproject.library_management_system_backend.model.Book;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookMapper {
    BookDto toBookDto(Book book);

    Book toEntity(CreateBookRequest bookDto);
}
