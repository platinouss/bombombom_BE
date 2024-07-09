package com.bombombom.devs.external.book.service;

import com.bombombom.devs.NaverClient;
import com.bombombom.devs.book.dto.IndexBookCommand;
import com.bombombom.devs.book.model.Book;
import com.bombombom.devs.dto.NaverBookApiQuery;
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

    private final BookReader bookReader;
    private final BookWriter bookWriter;
    private final NaverClient naverClient;

    public SearchBooksResult searchBook(SearchBookQuery searchBookQuery) {
        List<Book> books = bookReader.findBooks(searchBookQuery);
        return SearchBooksResult.builder().bookResults(
            books.stream().map(SearchBooksResult::fromBook).collect(Collectors.toList())).build();
    }

    public SearchBooksResult findBookUsingOpenApi(NaverBookApiQuery naverBookApiQuery) {
        return SearchBooksResult.fromNaverBookApiResult(naverClient.searchBooks(naverBookApiQuery));
    }

    public void addBook(AddBookCommand addBookCommand) {
        Book indexedBook = bookReader.findByIsbn(addBookCommand);
        if (Objects.nonNull(indexedBook.getBookId())) {
            return;
        }
        Book book = bookWriter.save(indexedBook);
        indexedBook.setBookId(book.getBookId());
        bookWriter.update(indexedBook);
    }

    public void indexBooks(List<IndexBookCommand> indexBookCommands) {
        bookWriter.upsertAll(indexBookCommands);
    }
}
