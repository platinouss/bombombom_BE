package com.bombombom.devs.external.book.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.bombombom.devs.NaverClient;
import com.bombombom.devs.book.model.Book;
import com.bombombom.devs.book.repository.BookRepository;
import com.bombombom.devs.dto.NaverBookApiQuery;
import com.bombombom.devs.dto.NaverBookApiResult;
import com.bombombom.devs.dto.NaverBookApiResult.SearchBookItem;
import com.bombombom.devs.external.book.enums.SearchOption;
import com.bombombom.devs.external.book.service.dto.AddBookCommand;
import com.bombombom.devs.external.book.service.dto.SearchBookQuery;
import com.bombombom.devs.external.book.service.dto.SearchBooksResult;
import com.bombombom.devs.external.book.service.dto.SearchBooksResult.BookResult;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    private BookService bookService;

    @InjectMocks
    private BookReader bookReader;

    @InjectMocks
    private BookWriter bookWriter;

    @Mock
    private NaverClient naverClient;

    @Mock
    private BookRepository bookRepository;

    @BeforeEach
    void setUp() {
        bookService = new BookService(bookReader, bookWriter, naverClient);
    }

    @DisplayName("통합 검색을 통해 서적명 또는 저자와 매칭되는 기술 서적을 검색할 수 있다.")
    @Test
    void search_book_by_title_or_author_success() {
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
            .imageUrl(
                "https://shopping-phinf.pstatic.net/main_3243601/32436011847.20221228073547.jpg")
            .tableOfContents("")
            .build()
        );
        BookResult bookResult = BookResult.builder()
            .title("자바 최적화(Optimizing Java)")
            .author("벤저민 J. 에번스^제임스 고프^크리스 뉴랜드")
            .publisher("한빛미디어")
            .isbn(9791162241776L)
            .imageUrl(
                "https://shopping-phinf.pstatic.net/main_3243601/32436011847.20221228073547.jpg")
            .tableOfContents("")
            .build();
        SearchBooksResult searchBooksResult = SearchBooksResult.builder()
            .bookResults(List.of(bookResult))
            .build();

        doReturn(queryResult).when(bookRepository)
            .findTop30ByTitleOrAuthor(searchBookQuery.keyword());

        /*
        When & Then
         */
        assertThat(bookService.searchBook(searchBookQuery)).isEqualTo(searchBooksResult);
        verify(bookRepository, times(1)).findTop30ByTitleOrAuthor(searchBookQuery.keyword());
    }

    @DisplayName("서적명 검색 조건으로 기술 서적을 검색할 수 있다.")
    @Test
    void search_book_by_title_success() {
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
            .imageUrl(
                "https://shopping-phinf.pstatic.net/main_3243601/32436011847.20221228073547.jpg")
            .tableOfContents("")
            .build()
        );
        BookResult bookResult = BookResult.builder()
            .title("자바 최적화(Optimizing Java)")
            .author("벤저민 J. 에번스^제임스 고프^크리스 뉴랜드")
            .publisher("한빛미디어")
            .isbn(9791162241776L)
            .imageUrl(
                "https://shopping-phinf.pstatic.net/main_3243601/32436011847.20221228073547.jpg")
            .tableOfContents("")
            .build();
        SearchBooksResult searchBooksResult = SearchBooksResult.builder()
            .bookResults(List.of(bookResult))
            .build();

        doReturn(queryResult).when(bookRepository).findTop30ByTitle(searchBookQuery.keyword());

        /*
        When & Then
         */
        assertThat(bookService.searchBook(searchBookQuery)).isEqualTo(searchBooksResult);
        verify(bookRepository, times(1)).findTop30ByTitle(searchBookQuery.keyword());
    }

    @DisplayName("저자 검색 조건으로 기술 서적을 검색할 수 있다.")
    @Test
    void search_book_by_author_success() {
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
            .imageUrl(
                "https://shopping-phinf.pstatic.net/main_3243601/32436011847.20221228073547.jpg")
            .tableOfContents("")
            .build()
        );
        BookResult bookResult = BookResult.builder()
            .title("자바 최적화(Optimizing Java)")
            .author("벤저민 J. 에번스^제임스 고프^크리스 뉴랜드")
            .publisher("한빛미디어")
            .isbn(9791162241776L)
            .imageUrl(
                "https://shopping-phinf.pstatic.net/main_3243601/32436011847.20221228073547.jpg")
            .tableOfContents("")
            .build();
        SearchBooksResult searchBooksResult = SearchBooksResult.builder()
            .bookResults(List.of(bookResult))
            .build();

        doReturn(queryResult).when(bookRepository).findTop30ByAuthor(searchBookQuery.keyword());

        /*
        When & Then
         */
        assertThat(bookService.searchBook(searchBookQuery)).isEqualTo(searchBooksResult);
        verify(bookRepository, times(1)).findTop30ByAuthor(
            searchBookQuery.keyword());
    }

    @DisplayName("NAVER Open API를 통해 서적을 검색할 수 있다.")
    @Test
    void search_book_using_naver_open_api_success() {
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
            SearchBooksResult.fromNaverBookApiResult(naverBookApiResult));
    }

    @DisplayName("새로운 서적 정보를 DB에 추가한다.")
    @Test
    void add_new_book_success() {
        /*
        Given
         */
        AddBookCommand addBookCommand = AddBookCommand.builder().isbn(9791158392703L).build();
        Optional<Book> indexedBook = Optional.of(Book.builder()
            .title("Real MySQL 8.0 (1권) (개발자와 DBA를 위한 MySQL 실전 가이드)")
            .author("백은빈")
            .publisher("위키북스")
            .isbn(9791158392703L)
            .imageUrl(
                "https://shopping-phinf.pstatic.net/main_3244397/32443973624.20230822103818.jpg")
            .tableOfContents("")
            .build());
        Book book = Book.builder()
            .bookId(10L)
            .title("Real MySQL 8.0 (1권) (개발자와 DBA를 위한 MySQL 실전 가이드)")
            .author("백은빈")
            .publisher("위키북스")
            .isbn(9791158392703L)
            .imageUrl(
                "https://shopping-phinf.pstatic.net/main_3244397/32443973624.20230822103818.jpg")
            .tableOfContents("")
            .build();

        doReturn(indexedBook).when(bookRepository).findIndexedBookByIsbn(anyLong());
        doReturn(book).when(bookRepository).save(ArgumentMatchers.any(Book.class));

        /*
        When
         */
        bookService.addBook(addBookCommand);

        /*
        Then
         */
        assertThat(indexedBook.get().getBookId()).isEqualTo(10L);
        verify(bookRepository, times(1)).findIndexedBookByIsbn(anyLong());
        verify(bookRepository, times(1)).save(any(Book.class));
        verify(bookRepository).save((any(Book.class)));
    }
}
