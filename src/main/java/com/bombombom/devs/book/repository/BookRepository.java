package com.bombombom.devs.book.repository;

import com.bombombom.devs.book.models.Book;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findBooksByTitleContainingOrAuthorContaining(String title, String author);
}
