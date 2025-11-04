package com.finalproject.library_management_system_backend.dtos;

import lombok.Data;

@Data
public class BookDto {
    private Long id;
    private String title;
    private String author;
    private Integer publicationYear;
    private Integer totalCopies;
    private Integer availableCopies;
    private String pictureUrl;
}