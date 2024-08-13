package org.example.testtasknerdysoft.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.testtasknerdysoft.entity.Book;
import org.example.testtasknerdysoft.entity.Member;
import org.example.testtasknerdysoft.exception.BookNotAvailableException;
import org.example.testtasknerdysoft.exception.BookNotFoundException;
import org.example.testtasknerdysoft.exception.MemberNotFoundException;
import org.example.testtasknerdysoft.exception.MemberWithBorrowedBooksException;
import org.example.testtasknerdysoft.repo.BookRepository;
import org.example.testtasknerdysoft.repo.MemberRepository;
import org.example.testtasknerdysoft.service.MemberService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;

    @Value("${library.max-books}")
    private int maxBooks;

    @Transactional
    public Member createOrUpdateMember(Member member) {
        return memberRepository.save(member);
    }

    public Member getMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException("Member not found with ID: " + id));
    }

    @Transactional
    public void deleteMember(Long memberId) {
        Member member = getMemberById(memberId);
        if (member.getBorrowedBooks().isEmpty()) {
            memberRepository.delete(member);
        } else {
            throw new MemberWithBorrowedBooksException("Member cannot be deleted because they have borrowed books.");
        }
    }

    @Transactional
    public void borrowBook(Long memberId, Long bookId) {
        Member member = getMemberById(memberId);
        Book book = getBookById(bookId);

        if (canBorrowBook(member, book)) {
            member.getBorrowedBooks().add(book);
            book.setAmount(book.getAmount() - 1);
            memberRepository.save(member);
            bookRepository.save(book);
        } else {
            throw new BookNotAvailableException("Cannot borrow book: exceeds limit or book is unavailable.");
        }
    }

    @Transactional
    public void returnBook(Long memberId, Long bookId) {
        Member member = getMemberById(memberId);
        Book book = getBookById(bookId);

        if (member.getBorrowedBooks().remove(book)) {
            book.setAmount(book.getAmount() + 1);
            memberRepository.save(member);
            bookRepository.save(book);
        } else {
            throw new BookNotAvailableException("Book was not borrowed by this member.");
        }
    }

    private Book getBookById(Long bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + bookId));
    }

    private boolean canBorrowBook(Member member, Book book) {
        return member.getBorrowedBooks().size() < maxBooks && book.getAmount() > 0;
    }

    public Set<Book> getBooksByMember(String name) {
        Member member = memberRepository.findByName(name);
        if (member != null) {
            return member.getBorrowedBooks();
        } else {
            throw new MemberNotFoundException("Member not found with name: " + name);
        }
    }

    public List<String> getDistinctBorrowedBookNames() {
        return memberRepository.findAll().stream()
                .flatMap(member -> member.getBorrowedBooks().stream())
                .map(Book::getTitle)
                .distinct()
                .collect(Collectors.toList());
    }

    public Map<String, Long> getBorrowedBooksSummary() {
        return memberRepository.findAll().stream()
                .flatMap(member -> member.getBorrowedBooks().stream())
                .collect(Collectors.groupingBy(Book::getTitle, Collectors.counting()));
    }
}
