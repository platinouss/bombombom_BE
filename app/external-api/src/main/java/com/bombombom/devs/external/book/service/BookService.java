package com.bombombom.devs.external.book.service;

import com.bombombom.devs.NaverClient;
import com.bombombom.devs.book.dto.IndexBookCommand;
import com.bombombom.devs.book.model.Book;
import com.bombombom.devs.book.repository.BookRepository;
import com.bombombom.devs.dto.NaverBookApiQuery;
import com.bombombom.devs.external.book.enums.SearchOption;
import com.bombombom.devs.external.book.exception.BookNotFoundException;
import com.bombombom.devs.external.book.service.dto.AddBookCommand;
import com.bombombom.devs.external.book.service.dto.SearchBookQuery;
import com.bombombom.devs.external.book.service.dto.SearchBooksResult;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookService {

    private final NaverClient naverClient;
    private final BookRepository bookRepository;

    public SearchBooksResult searchBook(SearchBookQuery searchBookQuery) {
        List<Book> books;
        if (searchBookQuery.searchOption() == SearchOption.TITLE) {
            books = bookRepository.findTop30ByTitle(searchBookQuery.keyword());
        } else if (searchBookQuery.searchOption() == SearchOption.AUTHOR) {
            books = bookRepository.findTop30ByAuthor(searchBookQuery.keyword());
        } else {
            books = bookRepository.findTop30ByTitleOrAuthor(searchBookQuery.keyword());
        }
        return SearchBooksResult.builder().bookResults(
            books.stream().map(SearchBooksResult::fromBook).collect(Collectors.toList())).build();
    }

    public SearchBooksResult findIndexedBook(NaverBookApiQuery naverBookApiQuery) {
        return SearchBooksResult.fromNaverBookApiResult(naverClient.searchBooks(naverBookApiQuery));
    }

    public void addBook(AddBookCommand addBookCommand) {
        Book indexedBook = bookRepository.findIndexedBookByIsbn(addBookCommand.isbn()).orElseThrow(
            BookNotFoundException::new);
        if (Objects.nonNull(indexedBook.getBookId())) {
            return;
        }
        Book book = bookRepository.save(indexedBook);
        indexedBook.setBookId(book.getBookId());
        bookRepository.update(book);
    }

    public void indexBooks(List<IndexBookCommand> indexBookCommands) {
        if (indexBookCommands.isEmpty()) {
            return;
        }
        bookRepository.upsertAll(indexBookCommands);
    }
}
