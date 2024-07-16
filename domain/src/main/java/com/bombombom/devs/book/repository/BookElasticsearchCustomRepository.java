package com.bombombom.devs.book.repository;

import com.bombombom.devs.book.model.BookDocument;
import com.bombombom.devs.book.model.vo.BookInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BookElasticsearchCustomRepository {

    private final ObjectMapper objectMapper;
    private final ElasticsearchOperations operations;

    public void upsertAll(List<BookInfo> bookInfos) {
        List<UpdateQuery> updateQueries = bookInfos.stream().map(bookInfo -> {
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

    public void deleteIndex() {
        operations.indexOps(BookDocument.class).delete();
    }
}
