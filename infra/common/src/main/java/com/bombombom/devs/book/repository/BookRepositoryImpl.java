package com.bombombom.devs.book.repository;

import com.bombombom.devs.book.model.Book;
import com.bombombom.devs.book.model.vo.BookInfo;
import com.bombombom.devs.model.BookDocument;
import com.bombombom.devs.model.BookEntity;
import com.bombombom.devs.repository.BookElasticsearchRepository;
import com.bombombom.devs.repository.BookJpaRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BookRepositoryImpl implements BookRepository {

    private final ObjectMapper objectMapper;
    private final BookJpaRepository bookJpaRepository;
    private final ElasticsearchOperations operations;
    private final BookElasticsearchRepository bookElasticsearchRepository;

    @Override
    public Optional<Book> findIndexedBookByIsbn(Long isbn) {
        return bookElasticsearchRepository.findByIsbn(isbn).map(BookDocument::toBook);
    }

    @Override
    public List<Book> findTop30ByTitle(String title) {
        List<BookDocument> bookDocuments = bookElasticsearchRepository.findTop30ByTitle(title);
        return bookDocuments.stream().map(BookDocument::toBook).toList();
    }

    @Override
    public List<Book> findTop30ByAuthor(String author) {
        List<BookDocument> bookDocuments = bookElasticsearchRepository.findTop30ByAuthor(author);
        return bookDocuments.stream().map(BookDocument::toBook).toList();
    }

    @Override
    public List<Book> findTop30ByTitleOrAuthor(String keyword) {
        List<BookDocument> bookDocuments = bookElasticsearchRepository.findTop30ByTitleOrAuthor(
            keyword, keyword);
        return bookDocuments.stream().map(BookDocument::toBook).toList();
    }

    @Override
    public Book save(Book book) {
        BookEntity bookEntity = bookJpaRepository.findByIsbn(book.getIsbn())
            .orElseGet(() -> bookJpaRepository.save(BookEntity.fromBook(book)));
        return BookEntity.toBook(bookEntity);
    }

    @Override
    public void update(Book book) {
        bookElasticsearchRepository.save(BookDocument.fromBook(book));
    }

    @Override
    public void upsertAll(List<Book> books) {
        List<UpdateQuery> updateQueries = books.stream().map(book -> {
            BookInfo bookInfo = BookInfo.fromBook(book);
            Document document;
            try {
                document = Document.parse(objectMapper.writeValueAsString(bookInfo));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("JSON 변환에 실패했습니다.");
            }
            return UpdateQuery.builder(String.valueOf(bookInfo.isbn()))
                .withDocument(document)
                .withDocAsUpsert(true)
                .build();
        }).toList();
        operations.bulkUpdate(updateQueries, BookDocument.class);
    }

    @Override
    public void deleteBookIndex() {
        operations.indexOps(BookDocument.class).delete();
    }
}
