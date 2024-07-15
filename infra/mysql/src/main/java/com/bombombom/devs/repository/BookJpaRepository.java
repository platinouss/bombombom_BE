package com.bombombom.devs.repository;

import com.bombombom.devs.model.BookEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookJpaRepository extends JpaRepository<BookEntity, Long> {

    Optional<BookEntity> findByIsbn(Long isbn);
}
