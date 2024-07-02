package com.bombombom.devs.book.repository;

import com.bombombom.devs.book.models.Book;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findTop30BooksByTitleContainingOrAuthorContaining(String title, String author);

    List<Book> findTop30BooksByTitleContaining(String title);

    List<Book> findTop30BooksByAuthorContaining(String author);

    Optional<Book> findByIsbn(Long isbn);

}
