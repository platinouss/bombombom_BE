package com.bombombom.devs.book.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.bombombom.devs.book.enums.SearchOption;
import com.bombombom.devs.book.models.Book;
import com.bombombom.devs.book.naverapi.NaverClient;
import com.bombombom.devs.book.repository.BookBulkRepository;
import com.bombombom.devs.book.repository.BookRepository;
import com.bombombom.devs.book.service.dto.AddBookCommand;
import com.bombombom.devs.book.service.dto.NaverBookApiQuery;
import com.bombombom.devs.book.service.dto.NaverBookApiResult;
import com.bombombom.devs.book.service.dto.NaverBookApiResult.SearchBookItem;
import com.bombombom.devs.book.service.dto.SearchBookQuery;
import com.bombombom.devs.book.service.dto.SearchBooksResult;
import com.bombombom.devs.book.service.dto.SearchBooksResult.BookResult;
import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @InjectMocks
    private BookService bookService;

    @Mock
    private NaverClient naverClient;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookBulkRepository bookBulkRepository;

    @DisplayName("통합 검색을 통해 서적명 또는 저자와 매칭되는 기술 서적을 검색할 수 있다.")
    @Test
    void test1() {
        /*
        Given
         */
        SearchBookQuery searchBookQuery = SearchBookQuery.builder()
            .keyword("자바 최적화(Optimizing Java)")
            .searchOption(SearchOption.TOTAL)
            .build();
        List<Book> queryResult = List.of(Book.builder()
            .title("자바 최적화(Optimizing Java)")
            .author("벤저민 J. 에번스^제임스 고프^크리스 뉴랜드")
            .publisher("한빛미디어")
            .isbn(9791162241776L)
            .tableOfContents("")
            .build()
        );
        BookResult bookResult = BookResult.builder()
            .title("자바 최적화(Optimizing Java)")
            .author("벤저민 J. 에번스^제임스 고프^크리스 뉴랜드")
            .publisher("한빛미디어")
            .isbn(9791162241776L)
            .tableOfContents("")
            .build();
        SearchBooksResult searchBooksResult = SearchBooksResult.builder()
            .booksResult(List.of(bookResult))
            .build();

        doReturn(queryResult).when(bookRepository)
            .findTop30BooksByTitleContainingOrAuthorContaining(searchBookQuery.keyword(),
                searchBookQuery.keyword());

        /*
        When & Then
         */
        assertThat(bookService.searchBook(searchBookQuery)).isEqualTo(searchBooksResult);
        verify(bookRepository, times(1)).findTop30BooksByTitleContainingOrAuthorContaining(
            searchBookQuery.keyword(), searchBookQuery.keyword());
    }

    @DisplayName("서적명 검색 조건으로 기술 서적을 검색할 수 있다.")
    @Test
    void test2() {
        /*
        Given
         */
        SearchBookQuery searchBookQuery = SearchBookQuery.builder()
            .keyword("자바 최적화(Optimizing Java)")
            .searchOption(SearchOption.TITLE)
            .build();
        List<Book> queryResult = List.of(Book.builder()
            .title("자바 최적화(Optimizing Java)")
            .author("벤저민 J. 에번스^제임스 고프^크리스 뉴랜드")
            .publisher("한빛미디어")
            .isbn(9791162241776L)
            .tableOfContents("")
            .build()
        );
        BookResult bookResult = BookResult.builder()
            .title("자바 최적화(Optimizing Java)")
            .author("벤저민 J. 에번스^제임스 고프^크리스 뉴랜드")
            .publisher("한빛미디어")
            .isbn(9791162241776L)
            .tableOfContents("")
            .build();
        SearchBooksResult searchBooksResult = SearchBooksResult.builder()
            .booksResult(List.of(bookResult))
            .build();

        doReturn(queryResult).when(bookRepository)
            .findTop30BooksByTitleContaining(searchBookQuery.keyword());

        /*
        When & Then
         */
        assertThat(bookService.searchBook(searchBookQuery)).isEqualTo(searchBooksResult);
        verify(bookRepository, times(1)).findTop30BooksByTitleContaining(
            searchBookQuery.keyword());
    }

    @DisplayName("저자 검색 조건으로 기술 서적을 검색할 수 있다.")
    @Test
    void test3() {
        /*
        Given
         */
        SearchBookQuery searchBookQuery = SearchBookQuery.builder()
            .keyword("자바 최적화(Optimizing Java)")
            .searchOption(SearchOption.AUTHOR)
            .build();
        List<Book> queryResult = List.of(Book.builder()
            .title("자바 최적화(Optimizing Java)")
            .author("벤저민 J. 에번스^제임스 고프^크리스 뉴랜드")
            .publisher("한빛미디어")
            .isbn(9791162241776L)
            .tableOfContents("")
            .build()
        );
        BookResult bookResult = BookResult.builder()
            .title("자바 최적화(Optimizing Java)")
            .author("벤저민 J. 에번스^제임스 고프^크리스 뉴랜드")
            .publisher("한빛미디어")
            .isbn(9791162241776L)
            .tableOfContents("")
            .build();
        SearchBooksResult searchBooksResult = SearchBooksResult.builder()
            .booksResult(List.of(bookResult))
            .build();

        doReturn(queryResult).when(bookRepository)
            .findTop30BooksByAuthorContaining(searchBookQuery.keyword());

        /*
        When & Then
         */
        assertThat(bookService.searchBook(searchBookQuery)).isEqualTo(searchBooksResult);
        verify(bookRepository, times(1)).findTop30BooksByAuthorContaining(
            searchBookQuery.keyword());
    }

    @DisplayName("NAVER Open API를 통해 서적을 검색할 수 있다.")
    @Test
    void test5() {
        /*
        Given
         */
        NaverBookApiQuery naverBookApiQuery = new NaverBookApiQuery(
            "Real MySQL 8.0 (1권) (개발자와 DBA를 위한 MySQL 실전 가이드)");
        SearchBookItem searchBookItem = SearchBookItem.builder()
            .title("Real MySQL 8.0 (1권) (개발자와 DBA를 위한 MySQL 실전 가이드)")
            .link("https://search.shopping.naver.com/book/catalog/32443973624")
            .image("https://shopping-phinf.pstatic.net/main_3244397/32443973624.20230822103818.jpg")
            .author("백은빈")
            .discount(27000)
            .publisher("위키북스")
            .pubdate("20210908")
            .isbn(9791158392703L)
            .description("MySQL 서버를 ...")
            .build();
        NaverBookApiResult naverBookApiResult = NaverBookApiResult.builder()
            .lastBuildDate(new Date())
            .total(1)
            .start(1)
            .display(1)
            .items(List.of(searchBookItem))
            .build();

        doReturn(naverBookApiResult).when(naverClient).searchBooks(naverBookApiQuery);

        /*
        When & Then
         */
        assertThat(bookService.findBookUsingOpenApi(naverBookApiQuery)).isEqualTo(
            naverBookApiResult);
    }

    @DisplayName("새로운 서적을 추가한다.")
    @Test
    void test4() {
        /*
        Given
         */
        AddBookCommand addBookCommand1 = AddBookCommand.builder()
            .title("Real MySQL 8.0 (1권) (개발자와 DBA를 위한 MySQL 실전 가이드)")
            .author("백은빈")
            .publisher("위키북스")
            .isbn(9791158392703L)
            .build();
        AddBookCommand addBookCommand2 = AddBookCommand.builder()
            .title("Real MySQL 8.0 (2권) (개발자와 DBA를 위한 MySQL 실전 가이드)")
            .author("백은빈")
            .publisher("위키북스")
            .isbn(9791158392727L)
            .build();
        List<AddBookCommand> addBookCommands = List.of(addBookCommand1, addBookCommand2);

        doNothing().when(bookBulkRepository).saveAll(anyList());

        /*
        When
         */
        bookService.addBooks(addBookCommands);

        /*
        Then
         */
        verify(bookBulkRepository, times(1)).saveAll(anyList());
    }
}
