package com.bombombom.devs.external.book.service;

import com.bombombom.devs.book.dto.IndexBookCommand;
import com.bombombom.devs.book.model.Book;
import com.bombombom.devs.book.repository.BookRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookWriter {

    private final BookRepository bookRepository;

    Book save(Book indexedBook) {
        return bookRepository.save(indexedBook);
    }

    void update(Book book) {
        bookRepository.update(book);
    }

    void upsertAll(List<IndexBookCommand> indexBookCommands) {
        if (indexBookCommands.isEmpty()) {
            return;
        }
        bookRepository.upsertAll(indexBookCommands);
    }

    void deleteIndex() {
        bookRepository.deleteBookIndex();
    }
}
