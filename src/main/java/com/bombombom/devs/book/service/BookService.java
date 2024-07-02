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
import java.util.NoSuchElementException;
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

    public SearchBooksResult searchBook(SearchBookQuery searchBookQuery) {
        List<BookDocument> bookDocuments;
        if (searchBookQuery.searchOption() == SearchOption.TITLE) {
            bookDocuments = bookElasticsearchRepository.findTop30ByTitle(searchBookQuery.keyword());
        } else if (searchBookQuery.searchOption() == SearchOption.AUTHOR) {
            bookDocuments = bookElasticsearchRepository.findTop30ByAuthor(
                searchBookQuery.keyword());
        } else {
            bookDocuments = bookElasticsearchRepository.findTop30ByTitleOrAuthor(
                searchBookQuery.keyword(), searchBookQuery.keyword());
        }
        return SearchBooksResult.builder().booksResult(
            bookDocuments.stream().map(SearchBooksResult::fromDocument)
                .collect(Collectors.toList())).build();
    }

    public NaverBookApiResult findBookUsingOpenApi(NaverBookApiQuery naverBookApiQuery) {
        return naverClient.searchBooks(naverBookApiQuery);
    }

    @Transactional
    public void addBook(AddBookCommand addBookCommand) {
        BookDocument bookDocument = bookElasticsearchRepository.findByIsbn(addBookCommand.isbn())
            .orElseThrow(() -> new NoSuchElementException("해당 서적이 존재하지 않습니다."));
        Book book = bookRepository.save(Book.fromDocument(bookDocument));
        bookDocument.setBookId(book.getId());
        bookElasticsearchRepository.save(bookDocument);
    }

    public void indexBooks(List<IndexBookCommand> indexBookCommands) {
        bookElasticsearchRepository.saveAll(
            indexBookCommands.stream().map(IndexBookCommand::toDocument).collect(
                Collectors.toList()));
    }
}
