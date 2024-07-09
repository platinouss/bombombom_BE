package com.bombombom.devs.repository;

import com.bombombom.devs.model.BookEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookJpaRepository extends JpaRepository<BookEntity, Long> {

}
