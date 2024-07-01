package com.bombombom.devs.book.repository;

import com.bombombom.devs.book.models.BookDocument;
import java.util.List;
import org.springframework.data.repository.ListCrudRepository;

public interface BookElasticsearchRepository extends ListCrudRepository<BookDocument, String> {

    List<BookDocument> findByTitle(String title);
}
