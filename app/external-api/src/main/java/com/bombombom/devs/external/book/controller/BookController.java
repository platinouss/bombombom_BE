package com.bombombom.devs.external.book.controller;

import com.bombombom.devs.external.book.controller.dto.BookAddRequest;
import com.bombombom.devs.external.book.controller.dto.BookIndexRequest;
import com.bombombom.devs.external.book.controller.dto.BookListRequest;
import com.bombombom.devs.external.book.controller.dto.BookListResponse;
import com.bombombom.devs.book.service.BookService;
import com.bombombom.devs.book.service.dto.NaverBookApiResult;
import com.bombombom.devs.book.service.dto.SearchBooksResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/books")
public class BookController {

    private final BookService bookService;

    @GetMapping
    public ResponseEntity<BookListResponse> bookList(@Valid BookListRequest bookListRequest) {
        SearchBooksResult searchBooksResult = bookService.searchBook(
            bookListRequest.toServiceDto());
        return ResponseEntity.ok().body(BookListResponse.fromResult(searchBooksResult));
    }

    @PostMapping
    public ResponseEntity<Void> addBook(
        @Valid @RequestBody BookAddRequest bookAddRequest) {
        bookService.addBook(bookAddRequest.toServiceDto());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/index")
    public ResponseEntity<BookListResponse> indexBooks(
        @Valid @RequestBody BookIndexRequest bookIndexRequest) {
        NaverBookApiResult naverBookApiResult = bookService.findBookUsingOpenApi(
            bookIndexRequest.toServiceDto());
        bookService.indexBooks(naverBookApiResult.toServiceDto());
        return ResponseEntity.ok().body(BookListResponse.fromResult(naverBookApiResult));
    }
}
