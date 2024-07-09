package com.bombombom.devs.book.repository;

import com.bombombom.devs.book.models.Book;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// TODO: study 도메인 모듈 전환 후 제거 필요
public interface BookTempRepository extends JpaRepository<Book, Long> {

    Optional<Book> findByIsbn(Long isbn);

}
