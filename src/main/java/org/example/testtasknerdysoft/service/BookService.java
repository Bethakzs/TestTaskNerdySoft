package org.example.testtasknerdysoft.service;

import org.example.testtasknerdysoft.entity.Book;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BookService {

    Book createOrUpdateBook(Book book);

    void deleteBook(Long bookId);

    List<Book> getAllBooks();
}
