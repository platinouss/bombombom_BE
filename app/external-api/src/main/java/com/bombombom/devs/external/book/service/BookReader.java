package com.bombombom.devs.external.book.service;

import com.bombombom.devs.book.model.Book;
import com.bombombom.devs.book.repository.BookRepository;
import com.bombombom.devs.external.book.enums.SearchOption;
import com.bombombom.devs.external.book.exception.BookNotFoundException;
import com.bombombom.devs.external.book.service.dto.AddBookCommand;
import com.bombombom.devs.external.book.service.dto.SearchBookQuery;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookReader {

    private final BookRepository bookRepository;

    List<Book> findBooks(SearchBookQuery searchBookQuery) {
        if (searchBookQuery.searchOption() == SearchOption.TITLE) {
            return bookRepository.findTop30ByTitle(searchBookQuery.keyword());
        }
        if (searchBookQuery.searchOption() == SearchOption.AUTHOR) {
            return bookRepository.findTop30ByAuthor(searchBookQuery.keyword());
        }
        return bookRepository.findTop30ByTitleOrAuthor(searchBookQuery.keyword());
    }

    Book findByIsbn(AddBookCommand addBookCommand) {
        return bookRepository.findIndexedBookByIsbn(addBookCommand.isbn())
            .orElseThrow(BookNotFoundException::new);
    }
}
