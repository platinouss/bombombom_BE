package com.bombombom.devs.book.repository;

import com.bombombom.devs.book.models.BookDocument;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.ListCrudRepository;

public interface BookElasticsearchRepository extends ListCrudRepository<BookDocument, String> {

    List<BookDocument> findTop30ByTitle(String title);

    List<BookDocument> findTop30ByAuthor(String author);

    List<BookDocument> findTop30ByTitleOrAuthor(String title, String author);

    Optional<BookDocument> findByIsbn(Long isbn);
}
