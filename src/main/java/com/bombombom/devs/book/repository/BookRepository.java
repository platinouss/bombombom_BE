package com.bombombom.devs.book.repository;

import com.bombombom.devs.book.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findTop30BooksByTitleContainingOrAuthorContaining(String title, String author);

    List<Book> findTop30BooksByTitleContaining(String title);

    List<Book> findTop30BooksByAuthorContaining(String author);
}
