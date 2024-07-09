package com.bombombom.devs.book.repository;

import com.bombombom.devs.book.dto.IndexBookCommand;
import com.bombombom.devs.book.model.Book;
import java.util.List;
import java.util.Optional;

public interface BookRepository {

    Book save(Book book);

    void update(Book book);

    void upsertAll(List<IndexBookCommand> indexBookCommands);

    void deleteBookIndex();

    Optional<Book> findIndexedBookByIsbn(Long isbn);

    List<Book> findTop30ByTitle(String title);

    List<Book> findTop30ByAuthor(String author);

    List<Book> findTop30ByTitleOrAuthor(String keyword);
}
