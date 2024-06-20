package com.bombombom.devs.book.repository;

import com.bombombom.devs.book.models.Book;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findTop50BooksByTitleContainingOrAuthorContaining(String title, String author);

    List<Book> findTop50BooksByTitleContaining(String title);

    List<Book> findTop50BooksByAuthorContaining(String author);
}
