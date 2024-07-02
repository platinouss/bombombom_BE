package com.bombombom.devs.book.repository;

import com.bombombom.devs.book.models.BookDocument;
import com.bombombom.devs.book.service.dto.IndexBookCommand;
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

    public void upsertAll(List<IndexBookCommand> indexBookCommands) {
        if (indexBookCommands.isEmpty()) {
            return;
        }
        List<UpdateQuery> updateQueries = indexBookCommands.stream().map(indexBookCommand -> {
            Document document;
            try {
                document = Document.parse(objectMapper.writeValueAsString(indexBookCommand));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("JSON 변환에 실패했습니다.");
            }
            return UpdateQuery.builder(String.valueOf(indexBookCommand.isbn()))
                .withDocument(document)
                .withDocAsUpsert(true)
                .build();
        }).toList();
        operations.bulkUpdate(updateQueries, BookDocument.class);
    }
}
