package com.bombombom.devs.external.book.service;

import com.bombombom.devs.NaverClient;
import com.bombombom.devs.book.model.Book;
import com.bombombom.devs.book.model.BookDocument;
import com.bombombom.devs.book.repository.BookElasticsearchCustomRepository;
import com.bombombom.devs.book.repository.BookElasticsearchRepository;
import com.bombombom.devs.book.repository.BookRepository;
import com.bombombom.devs.dto.NaverBookApiQuery;
import com.bombombom.devs.external.book.enums.SearchOption;
import com.bombombom.devs.external.book.exception.BookNotFoundException;
import com.bombombom.devs.external.book.service.dto.AddBookCommand;
import com.bombombom.devs.external.book.service.dto.IndexBookCommand;
import com.bombombom.devs.external.book.service.dto.SearchBookQuery;
import com.bombombom.devs.external.book.service.dto.SearchBooksResult;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookService {

    private final NaverClient naverClient;
    private final BookRepository bookRepository;
    private final BookElasticsearchRepository bookElasticsearchRepository;
    private final BookElasticsearchCustomRepository bookElasticsearchCustomRepository;

    @Transactional(readOnly = true)
    public SearchBooksResult searchBook(SearchBookQuery searchBookQuery) {
        List<BookDocument> books;
        if (searchBookQuery.searchOption() == SearchOption.TITLE) {
            books = bookElasticsearchRepository.findTop30ByTitle(searchBookQuery.keyword());
        } else if (searchBookQuery.searchOption() == SearchOption.AUTHOR) {
            books = bookElasticsearchRepository.findTop30ByAuthor(searchBookQuery.keyword());
        } else {
            books = bookElasticsearchRepository.findTop30ByTitleOrAuthor(searchBookQuery.keyword(),
                searchBookQuery.keyword());
        }
        return SearchBooksResult.builder().bookResults(
                books.stream().map(SearchBooksResult::fromBookDocument).collect(Collectors.toList()))
            .build();
    }

    public SearchBooksResult findIndexedBook(NaverBookApiQuery naverBookApiQuery) {
        return SearchBooksResult.fromNaverBookApiResult(naverClient.searchBooks(naverBookApiQuery));
    }

    @Transactional
    public void addBook(AddBookCommand addBookCommand) {
        BookDocument indexedBook = bookElasticsearchRepository.findByIsbn(addBookCommand.isbn())
            .orElseThrow(BookNotFoundException::new);
        if (Objects.nonNull(indexedBook.getBookId())) {
            return;
        }
        Book book = bookRepository.findByIsbn(indexedBook.getIsbn())
            .orElseGet(() -> bookRepository.save(Book.fromBookDocument(indexedBook)));
        indexedBook.setBookId(book.getId());
        bookElasticsearchRepository.save(indexedBook);
    }

    public void indexBooks(List<IndexBookCommand> indexBookCommands) {
        if (indexBookCommands.isEmpty()) {
            return;
        }
        bookElasticsearchCustomRepository.upsertAll(
            indexBookCommands.stream().map(IndexBookCommand::toBookInfo).toList());
    }
}
