package com.bombombom.devs.book;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bombombom.devs.book.controller.BookController;
import com.bombombom.devs.book.controller.dto.BookAddRequest;
import com.bombombom.devs.book.controller.dto.BookIndexRequest;
import com.bombombom.devs.book.controller.dto.BookListResponse;
import com.bombombom.devs.book.enums.SearchOption;
import com.bombombom.devs.book.models.BookDocument;
import com.bombombom.devs.book.repository.BookElasticsearchRepository;
import com.bombombom.devs.book.service.dto.SearchBooksResult;
import com.bombombom.devs.book.service.dto.SearchBooksResult.BookResult;
import com.bombombom.devs.config.ElasticsearchTestConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.filter.CharacterEncodingFilter;

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest
@Import(ElasticsearchTestConfig.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class BookIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookController bookController;

    @Autowired
    private BookElasticsearchRepository bookElasticsearchRepository;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookController)
            .addFilter(new CharacterEncodingFilter("UTF-8", true))
            .build();
        BookDocument bookDocument = BookDocument.builder()
            .id("1")
            .title("SWM 봄봄봄 테스트 책")
            .author("저자")
            .publisher("출판사")
            .isbn(1L)
            .build();
        bookElasticsearchRepository.save(bookDocument);
    }

    @DisplayName("Elasticsearch에 존재하는 서적을 검색할 수 있다.")
    @Test
    void search_book_in_database_success() throws Exception {
        /*
        Given
         */
        String keyword = "SWM 봄봄봄 테스트 책";
        String searchOption = SearchOption.TOTAL.name();
        SearchBooksResult.BookResult bookResult = SearchBooksResult.BookResult.builder()
            .title("SWM 봄봄봄 테스트 책")
            .author("저자")
            .publisher("출판사")
            .isbn(1L)
            .build();
        SearchBooksResult searchBooksResult = SearchBooksResult.builder()
            .booksResult(List.of(bookResult))
            .build();

        /*
        When
         */
        ResultActions resultActions = mockMvc.perform(get("/api/v1/books")
            .param("keyword", keyword)
            .param("searchOption", searchOption)
        );

        /*
        Then
         */
        resultActions.andExpect(status().isOk())
            .andExpect(content().string(objectMapper.writeValueAsString(
                BookListResponse.fromResult(searchBooksResult))));
    }

    @DisplayName("NAVER Open API를 통해 서적을 검색하고, 해당 서적 정보를 Elasticsearch에 저장 후 반환한다.")
    @Test
    void fetch_book_using_naver_open_api_and_save_to_elasticsearch() throws Exception {
        /*
        Given
         */
        BookIndexRequest bookIndexRequest = BookIndexRequest.builder()
            .keyword("자바 최적화(Optimizing Java)")
            .build();
        BookResult bookResult = BookResult.builder()
            .title("자바 최적화(Optimizing Java) (가장 빠른 성능을 구현하는 검증된 10가지 기법)")
            .author("벤저민 J. 에번스^제임스 고프^크리스 뉴랜드")
            .publisher("한빛미디어")
            .isbn(9791162241776L)
            .tableOfContents("")
            .imageUrl(
                "https://shopping-phinf.pstatic.net/main_3243601/32436011847.20221228073547.jpg")
            .build();
        SearchBooksResult searchBooksResult = SearchBooksResult.builder()
            .booksResult(List.of(bookResult))
            .build();

        /*
        When
         */
        ResultActions resultActions = mockMvc.perform(post("/api/v1/books/index")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(bookIndexRequest))
        );

        /*
        Then
         */
        resultActions
            .andExpect(status().isOk())
            .andExpect(content().string(
                objectMapper.writeValueAsString(BookListResponse.fromResult(searchBooksResult))));
    }

    @DisplayName("기술 서적 스터디에서 선정된 서적은 DB에 저장된다.")
    @Test
    void save_selected_book_to_database() throws Exception {
        /*
        Given
         */
        BookAddRequest bookAddRequest = BookAddRequest.builder()
            .isbn("1")
            .build();

        /*
        When
         */
        ResultActions resultActions = mockMvc.perform(post("/api/v1/books")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(bookAddRequest))
        );

        /*
        Then
         */
        resultActions.andExpect(status().isOk());
    }
}
