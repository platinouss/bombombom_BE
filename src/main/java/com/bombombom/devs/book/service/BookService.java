package com.bombombom.devs.book.service;

import com.bombombom.devs.book.enums.SearchOption;
import com.bombombom.devs.book.models.Book;
import com.bombombom.devs.book.models.BookDocument;
import com.bombombom.devs.book.repository.BookBulkRepository;
import com.bombombom.devs.book.repository.BookElasticsearchRepository;
import com.bombombom.devs.book.repository.BookRepository;
import com.bombombom.devs.book.service.dto.AddBookCommand;
import com.bombombom.devs.book.service.dto.NaverBookApiQuery;
import com.bombombom.devs.book.service.dto.NaverBookApiResult;
import com.bombombom.devs.book.service.dto.SearchBookQuery;
import com.bombombom.devs.book.service.dto.SearchBooksResult;
import com.bombombom.devs.client.naver.NaverClient;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookService {

    private final NaverClient naverClient;
    private final BookRepository bookRepository;
    private final BookBulkRepository bookBulkRepository;
    private final BookElasticsearchRepository bookElasticsearchRepository;

    @Transactional(readOnly = true)
    public SearchBooksResult searchBook(SearchBookQuery searchBookQuery) {
        List<Book> books;
        if (searchBookQuery.searchOption() == SearchOption.TITLE) {
            books = bookRepository.findTop30BooksByTitleContaining(searchBookQuery.keyword());
        } else if (searchBookQuery.searchOption() == SearchOption.AUTHOR) {
            books = bookRepository.findTop30BooksByAuthorContaining(searchBookQuery.keyword());
        } else {
            books = bookRepository.findTop30BooksByTitleContainingOrAuthorContaining(
                searchBookQuery.keyword(), searchBookQuery.keyword());
        }
        return SearchBooksResult.builder().booksResult(
            books.stream().map(SearchBooksResult::fromEntity).collect(Collectors.toList())).build();
    }

    public NaverBookApiResult findBookUsingOpenApi(NaverBookApiQuery naverBookApiQuery) {
        return naverClient.searchBooks(naverBookApiQuery);
    }

    public void addBooks(List<AddBookCommand> addBookCommand) {
        bookBulkRepository.saveAll(
            addBookCommand.stream().map(AddBookCommand::toEntity).collect(Collectors.toList()));
    }

    public SearchBooksResult searchBookUsingES(SearchBookQuery searchBookQuery) {
        List<BookDocument> bookDocuments = bookElasticsearchRepository.findByTitle(
            searchBookQuery.keyword());
        return SearchBooksResult.builder().booksResult(
            bookDocuments.stream().map(SearchBooksResult::fromDocument)
                .collect(Collectors.toList())).build();
    }

    public void addBooksUsingEs(List<AddBookCommand> addBookCommands) {
        bookElasticsearchRepository.saveAll(
            addBookCommands.stream().map(AddBookCommand::toDocument).collect(
                Collectors.toList()));
    }
}
