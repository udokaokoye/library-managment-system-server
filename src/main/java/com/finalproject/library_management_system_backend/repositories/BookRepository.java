package com.finalproject.library_management_system_backend.repositories;

import com.finalproject.library_management_system_backend.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {



}
