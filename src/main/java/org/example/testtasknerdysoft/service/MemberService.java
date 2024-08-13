package org.example.testtasknerdysoft.service;

import org.example.testtasknerdysoft.entity.Book;
import org.example.testtasknerdysoft.entity.Member;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public interface MemberService {
    Member createOrUpdateMember(Member member);

    Member getMemberById(Long id);

    void deleteMember(Long id);

    void borrowBook(Long memberId, Long bookId);

    void returnBook(Long memberId, Long bookId);

    Set<Book> getBooksByMember(String name);

    List<String> getDistinctBorrowedBookNames();

    Map<String, Long> getBorrowedBooksSummary();
}
