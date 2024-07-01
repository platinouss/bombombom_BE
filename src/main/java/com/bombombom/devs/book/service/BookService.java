package com.bombombom.devs.book.service;

import com.bombombom.devs.book.enums.SearchOption;
import com.bombombom.devs.book.models.Book;
import com.bombombom.devs.book.models.BookDocument;
import com.bombombom.devs.book.repository.BookElasticsearchRepository;
import com.bombombom.devs.book.repository.BookRepository;
import com.bombombom.devs.book.service.dto.AddBookCommand;
import com.bombombom.devs.book.service.dto.IndexBookCommand;
import com.bombombom.devs.book.service.dto.NaverBookApiQuery;
import com.bombombom.devs.book.service.dto.NaverBookApiResult;
import com.bombombom.devs.book.service.dto.SearchBookQuery;
import com.bombombom.devs.book.service.dto.SearchBooksResult;
import com.bombombom.devs.client.naver.NaverClient;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookService {

    private final NaverClient naverClient;
    private final BookRepository bookRepository;
    private final BookElasticsearchRepository bookElasticsearchRepository;

    public SearchBooksResult searchBook(SearchBookQuery searchBookQuery) {
        List<BookDocument> bookDocuments;
        if (searchBookQuery.searchOption() == SearchOption.TITLE) {
            bookDocuments = bookElasticsearchRepository.findByTitle(searchBookQuery.keyword());
        } else if (searchBookQuery.searchOption() == SearchOption.AUTHOR) {
            bookDocuments = bookElasticsearchRepository.findByAuthor(searchBookQuery.keyword());
        } else {
            bookDocuments = bookElasticsearchRepository.findByTitleOrAuthor(
                searchBookQuery.keyword(), searchBookQuery.keyword());
        }
        return SearchBooksResult.builder().booksResult(
            bookDocuments.stream().map(SearchBooksResult::fromDocument)
                .collect(Collectors.toList())).build();
    }

    public NaverBookApiResult findBookUsingOpenApi(NaverBookApiQuery naverBookApiQuery) {
        return naverClient.searchBooks(naverBookApiQuery);
    }

    public void addBook(AddBookCommand addBookCommand) {
        Optional<BookDocument> bookDocument = bookElasticsearchRepository.findById(
            addBookCommand.isbn());
        bookDocument.ifPresent(document -> bookRepository.save(Book.fromDocument(document)));
    }

    public void indexBooks(List<IndexBookCommand> indexBookCommands) {
        for (IndexBookCommand indexBookCommand : indexBookCommands) {
            System.out.println(indexBookCommand.imageUrl());
        }
        bookElasticsearchRepository.saveAll(
            indexBookCommands.stream().map(IndexBookCommand::toDocument).collect(
                Collectors.toList()));
    }
}
