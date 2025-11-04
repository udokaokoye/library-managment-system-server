package com.finalproject.library_management_system_backend.model;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "books")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Getter
@Setter
@Data
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="title", nullable = false)
    private String title;

    @Column(name="author", nullable = false)
    private String author;

    @Column(name="publication_year", nullable = false)
    private Integer publicationYear;

    @Column(name="total_copies", nullable = false)
    private Integer totalCopies;

    @Column(name="available_copies", nullable = false)
    private Integer availableCopies;

    @Column(name="picture_url")
    private String pictureUrl;
}
