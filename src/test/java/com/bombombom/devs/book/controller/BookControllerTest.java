package com.bombombom.devs.book.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bombombom.devs.book.controller.dto.BookAddRequest;
import com.bombombom.devs.book.controller.dto.BookListResponse;
import com.bombombom.devs.book.service.BookService;
import com.bombombom.devs.book.service.dto.NaverBookApiQuery;
import com.bombombom.devs.book.service.dto.NaverBookApiResult;
import com.bombombom.devs.book.service.dto.NaverBookApiResult.SearchBookItem;
import com.bombombom.devs.book.service.dto.SearchBookQuery;
import com.bombombom.devs.book.service.dto.SearchBooksResult;
import com.bombombom.devs.book.service.dto.SearchBooksResult.BookResult;
import com.bombombom.devs.global.security.JwtUtils;
import com.bombombom.devs.global.util.SystemClock;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.filter.CharacterEncodingFilter;

@ActiveProfiles("test")
@WebMvcTest(BookController.class)
@Import({JwtUtils.class, SystemClock.class})
public class BookControllerTest {

    @Autowired
    private BookController bookController;

    @MockBean
    private BookService bookService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookController)
            .addFilter(new CharacterEncodingFilter("UTF-8", true))
            .build();
    }

    @DisplayName("서적을 검색할 수 있다.")
    @Test
    void test1() throws Exception {
        /*
        Given
         */
        String keyword = "대규모 시스템 설계 기초";
        String searchOption = "TOTAL";
        BookResult bookResult1 = BookResult.builder()
            .title("가상 면접 사례로 배우는 대규모 시스템 설계 기초")
            .author("알렉스 쉬")
            .publisher("인사이트")
            .isbn(9788966263158L)
            .build();
        BookResult bookResult2 = BookResult.builder()
            .title("가상 면접 사례로 배우는 대규모 시스템 설계 기초2")
            .author("알렉스 쉬^산 람")
            .publisher("인사이트")
            .isbn(9788966264254L)
            .build();
        SearchBooksResult searchBooksResult = SearchBooksResult.builder()
            .booksResult(List.of(bookResult1, bookResult2))
            .build();

        doReturn(searchBooksResult).when(bookService).searchBook(any(SearchBookQuery.class));

        /*
        When
         */
        ResultActions resultActions = mockMvc.perform(
            get("/api/v1/books")
                .param("keyword", keyword)
                .param("searchOption", searchOption)
        );

        /*
        Then
         */
        resultActions.andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(
                BookListResponse.fromResult(searchBooksResult))));
    }

    @DisplayName("지원하지 않는 검색 조건이면 검색이 실패한다.")
    @Test
    void test4() throws Exception {
        /*
        Given
         */
        String keyword = "대규모 시스템 설계 기초";
        String searchOption = "TEST";

        /*
        When
         */
        ResultActions resultActions = mockMvc.perform(
            get("/api/v1/books")
                .param("keyword", keyword)
                .param("searchOption", searchOption)
        );

        /*
        Then
         */
        resultActions.andExpect(status().isBadRequest());
    }

    @DisplayName("검색 키워드가 빈 경우 검색이 실패한다.")
    @Test
    void test6() throws Exception {
        /*
        Given
         */
        String keyword = " ";
        String searchOption = "TOTAL";

        /*
        When
         */
        ResultActions resultActions = mockMvc.perform(
            get("/api/v1/books")
                .param("keyword", keyword)
                .param("searchOption", searchOption)
        );

        /*
        Then
         */
        resultActions.andExpect(status().isBadRequest());
    }

    @DisplayName("검색 키워드와 함께 NAVER Open API를 호출하여 서적 정보를 저장하고 반환한다.")
    @Test
    void test7() throws Exception {
        /*
        Given
         */
        BookAddRequest bookAddRequest = BookAddRequest.builder()
            .keyword("자바 최적화(Optimizing Java)")
            .build();
        SearchBookItem searchBookItem = SearchBookItem.builder()
            .title("자바 최적화(Optimizing Java) (가장 빠른 성능을 구현하는 검증된 10가지 기법)")
            .link("https://search.shopping.naver.com/book/catalog/32436011847")
            .image("https://shopping-phinf.pstatic.net/main_3243601/32436011847.20221228073547.jpg")
            .author("벤저민 J. 에번스^제임스 고프^크리스 뉴랜드")
            .discount(28900)
            .publisher("한빛미디어")
            .pubdate("20190429")
            .isbn(9791162241776L)
            .description("자바 ...")
            .build();
        NaverBookApiResult naverBookApiResult = NaverBookApiResult.builder()
            .lastBuildDate(new Date())
            .total(1)
            .start(1)
            .display(1)
            .items(List.of(searchBookItem))
            .build();
        BookResult bookResult = BookResult.builder()
            .title("자바 최적화(Optimizing Java) (가장 빠른 성능을 구현하는 검증된 10가지 기법)")
            .author("벤저민 J. 에번스^제임스 고프^크리스 뉴랜드")
            .publisher("한빛미디어")
            .isbn(9791162241776L)
            .tableOfContents("")
            .build();
        SearchBooksResult searchBooksResult = SearchBooksResult.builder()
            .booksResult(List.of(bookResult))
            .build();

        doReturn(naverBookApiResult).when(bookService)
            .findBookUsingOpenApi(any(NaverBookApiQuery.class));

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
        resultActions
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.content().string(
                objectMapper.writeValueAsString(BookListResponse.fromResult(searchBooksResult))));
        verify(bookService, times(1)).findBookUsingOpenApi(any(NaverBookApiQuery.class));
        verify(bookService, times(1)).addBooks(naverBookApiResult.toServiceDto());
    }

    @DisplayName("검색 키워드가 빈 경우 NAVER Open API를 호출에 실패한다.")
    @Test
    void test8() throws Exception {
        /*
        Given
         */
        BookAddRequest bookAddRequest = BookAddRequest.builder()
            .keyword(" ")
            .build();

        /*
        When
         */
        ResultActions resultActions = mockMvc.perform(
            post("/api/v1/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookAddRequest))
        );

        /*
        Then
         */
        resultActions.andExpect(status().isBadRequest());
    }
}
