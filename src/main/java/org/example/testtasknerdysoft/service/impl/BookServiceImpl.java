package org.example.testtasknerdysoft.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.testtasknerdysoft.entity.Book;
import org.example.testtasknerdysoft.exception.BookNotAvailableException;
import org.example.testtasknerdysoft.exception.BookNotFoundException;
import org.example.testtasknerdysoft.repo.BookRepository;
import org.example.testtasknerdysoft.repo.MemberRepository;
import org.example.testtasknerdysoft.service.BookService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Book createOrUpdateBook(Book book) {
        Book existingBook = bookRepository.findByTitleAndAuthor(book.getTitle(), book.getAuthor());
        if (existingBook != null) {
            existingBook.setAmount(existingBook.getAmount() + 1);
            return bookRepository.save(existingBook);
        }
        return bookRepository.save(book);
    }

    @Transactional
    public void deleteBook(Long bookId) {
        Book book = getBookById(bookId);

        if (memberRepository.existsByBorrowedBooks(bookId)) {
            throw new BookNotAvailableException("Book cannot be deleted because it is borrowed.");
        }

        if (book.getAmount() > 0) {
            book.setAmount(book.getAmount() - 1);
            bookRepository.save(book);
        } else {
            bookRepository.delete(book);
        }
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book getBookById(Long bookId) {
        return bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("Book not found with ID: " + bookId));
    }
}
