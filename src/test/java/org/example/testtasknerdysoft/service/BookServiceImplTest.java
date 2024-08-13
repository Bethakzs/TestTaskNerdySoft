package org.example.testtasknerdysoft.service;

import org.example.testtasknerdysoft.entity.Book;
import org.example.testtasknerdysoft.exception.BookNotAvailableException;
import org.example.testtasknerdysoft.exception.BookNotFoundException;
import org.example.testtasknerdysoft.repo.BookRepository;
import org.example.testtasknerdysoft.repo.MemberRepository;
import org.example.testtasknerdysoft.service.impl.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @Transactional
    void createOrUpdateBook_ShouldUpdateExistingBook() {
        Book existingBook = new Book();
        existingBook.setTitle("Test Book");
        existingBook.setAuthor("Author");
        existingBook.setAmount(1);

        Book book = new Book();
        book.setTitle("Test Book");
        book.setAuthor("Author");

        when(bookRepository.findByTitleAndAuthor(anyString(), anyString())).thenReturn(existingBook);
        when(bookRepository.save(any(Book.class))).thenReturn(existingBook);

        Book result = bookService.createOrUpdateBook(book);

        assertEquals(2, result.getAmount());
        verify(bookRepository).save(existingBook);
    }

    @Test
    @Transactional
    void deleteBook_ShouldDecreaseAmountIfAvailable() {
        Book book = new Book();
        book.setAmount(1);
        book.setId(1L);

        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        when(memberRepository.existsByBorrowedBooks(anyLong())).thenReturn(false);
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        bookService.deleteBook(1L);

        assertEquals(0, book.getAmount());
        verify(bookRepository).save(book);
    }

    @Test
    void deleteBook_ShouldThrowExceptionIfBorrowed() {
        Book book = new Book();
        book.setId(1L);

        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        when(memberRepository.existsByBorrowedBooks(anyLong())).thenReturn(true);

        assertThrows(BookNotAvailableException.class, () -> bookService.deleteBook(1L));
    }

    @Test
    void getBookById_ShouldThrowExceptionIfNotFound() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> bookService.getBookById(1L));
    }
}

