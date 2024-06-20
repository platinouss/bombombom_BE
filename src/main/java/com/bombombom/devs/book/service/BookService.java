package com.bombombom.devs.book.service;

import com.bombombom.devs.book.models.Book;
import com.bombombom.devs.book.naverapi.NaverClient;
import com.bombombom.devs.book.repository.BookRepository;
import com.bombombom.devs.book.service.dto.AddBookCommand;
import com.bombombom.devs.book.service.dto.NaverBookApiQuery;
import com.bombombom.devs.book.service.dto.NaverBookApiResult;
import com.bombombom.devs.book.service.dto.SearchBookQuery;
import com.bombombom.devs.book.service.dto.SearchBooksResult;
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

    @Transactional(readOnly = true)
    public SearchBooksResult searchBook(SearchBookQuery searchBookQuery) {
        List<Book> books = bookRepository.findBooksByTitleContainingOrAuthorContaining(
            searchBookQuery.keyword(), searchBookQuery.keyword());
        return SearchBooksResult.builder().booksResult(
            books.stream().map(SearchBooksResult::fromEntity).collect(Collectors.toList())).build();
    }

    public NaverBookApiResult findBookUsingOpenApi(NaverBookApiQuery naverBookApiQuery) {
        return naverClient.searchBooks(naverBookApiQuery);
    }

    public void addBooks(List<AddBookCommand> addBookCommand) {
        bookRepository.saveAll(
            addBookCommand.stream().map(AddBookCommand::toEntity).collect(Collectors.toList()));
    }
}
