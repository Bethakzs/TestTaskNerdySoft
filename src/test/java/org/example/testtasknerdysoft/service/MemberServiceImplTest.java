package org.example.testtasknerdysoft.service;

import org.example.testtasknerdysoft.entity.Book;
import org.example.testtasknerdysoft.entity.Member;
import org.example.testtasknerdysoft.exception.BookNotAvailableException;
import org.example.testtasknerdysoft.exception.MemberNotFoundException;
import org.example.testtasknerdysoft.exception.MemberWithBorrowedBooksException;
import org.example.testtasknerdysoft.repo.BookRepository;
import org.example.testtasknerdysoft.repo.MemberRepository;
import org.example.testtasknerdysoft.service.impl.MemberServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class MemberServiceImplTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private MemberServiceImpl memberService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createOrUpdateMember_ShouldSaveMember() {
        Member member = new Member();
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        Member result = memberService.createOrUpdateMember(member);

        assertNotNull(result);
        verify(memberRepository).save(member);
    }

    @Test
    void getMemberById_ShouldThrowExceptionIfNotFound() {
        when(memberRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(MemberNotFoundException.class, () -> memberService.getMemberById(1L));
    }

    @Test
    void deleteMember_ShouldThrowExceptionIfHasBorrowedBooks() {
        Member member = new Member();
        member.setBorrowedBooks(new HashSet<>(List.of(new Book())));

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));

        assertThrows(MemberWithBorrowedBooksException.class, () -> memberService.deleteMember(1L));
    }

    @Test
    void borrowBook_ShouldAddBookToMemberAndDecreaseAmount() {
        Member member = new Member();
        member.setBorrowedBooks(new HashSet<>());

        Book book = new Book();
        book.setId(1L);
        book.setAmount(1);

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        when(memberRepository.save(any(Member.class))).thenReturn(member);
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        ReflectionTestUtils.setField(memberService, "maxBooks", 10);

        memberService.borrowBook(1L, 1L);

        Set<Book> borrowedBooks = member.getBorrowedBooks();
        assertTrue(borrowedBooks.stream().anyMatch(b -> b.getId().equals(book.getId())));
        assertEquals(0, book.getAmount());
        verify(memberRepository).save(member);
        verify(bookRepository).save(book);
    }

    @Test
    void returnBook_ShouldRemoveBookFromMemberAndIncreaseAmount() {
        Member member = new Member();
        Book book = new Book();
        member.setBorrowedBooks(new HashSet<>(List.of(book)));
        book.setAmount(0);

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));
        when(memberRepository.save(any(Member.class))).thenReturn(member);
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        memberService.returnBook(1L, 1L);

        assertFalse(member.getBorrowedBooks().contains(book));
        assertEquals(1, book.getAmount());
        verify(memberRepository).save(member);
        verify(bookRepository).save(book);
    }

    @Test
    void returnBook_ShouldThrowExceptionIfNotBorrowed() {
        Member member = new Member();
        member.setBorrowedBooks(new HashSet<>());

        Book book = new Book();

        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(book));

        assertThrows(BookNotAvailableException.class, () -> memberService.returnBook(1L, 1L));
    }

    @Test
    void getBooksByMember_ShouldThrowExceptionIfNotFound() {
        when(memberRepository.findByName(anyString())).thenReturn(null);

        assertThrows(MemberNotFoundException.class, () -> memberService.getBooksByMember("John"));
    }

    @Test
    void getDistinctBorrowedBookNames_ShouldReturnDistinctTitles() {
        Member member = new Member();
        Book book1 = new Book();
        Book book2 = new Book();
        book1.setTitle("Book1");
        book2.setTitle("Book2");
        member.setBorrowedBooks(Set.of(book1, book2));

        when(memberRepository.findAll()).thenReturn(List.of(member));

        List<String> titles = memberService.getDistinctBorrowedBookNames();

        assertEquals(Set.of("Book1", "Book2"), new HashSet<>(titles));
    }

    @Test
    void getBorrowedBooksSummary_ShouldReturnBookCounts() {
        Member member = new Member();
        Book book = new Book();
        book.setTitle("Book1");
        member.setBorrowedBooks(Set.of(book));

        when(memberRepository.findAll()).thenReturn(List.of(member));

        Map<String, Long> summary = memberService.getBorrowedBooksSummary();

        assertEquals(1, summary.get("Book1"));
    }
}
