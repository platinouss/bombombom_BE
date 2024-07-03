package com.bombombom.devs.book.repository;

import com.bombombom.devs.book.models.Book;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(Long isbn);

}
