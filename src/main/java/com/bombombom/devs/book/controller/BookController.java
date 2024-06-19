package com.bombombom.devs.book.controller;

import com.bombombom.devs.book.controller.dto.BookAddRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/books")
public class BookController {

    @PostMapping
    public void bookAdd(@Valid @RequestBody BookAddRequest bookAddRequest) {
    }
}
