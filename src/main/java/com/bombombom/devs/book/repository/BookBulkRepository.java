package com.bombombom.devs.book.repository;

import com.bombombom.devs.book.models.Book;
import java.sql.PreparedStatement;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class BookBulkRepository {

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void saveAll(List<Book> books) {
        String sql = "INSERT IGNORE INTO BOOK (title, author, publisher, isbn, table_of_contents)"
            + "VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.batchUpdate(sql, books, books.size(), (PreparedStatement ps, Book book) -> {
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getPublisher());
            ps.setLong(4, book.getIsbn());
            ps.setString(5, book.getTableOfContents());
        });
    }
}
