package org.example.testtasknerdysoft.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.testtasknerdysoft.entity.Book;
import org.example.testtasknerdysoft.entity.Member;
import org.example.testtasknerdysoft.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<Member> createMember(@Valid @RequestBody Member member) {
        Member createdMember = memberService.createOrUpdateMember(member);
        return ResponseEntity.ok(createdMember);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Member> getMemberById(@PathVariable Long id) {
        Member member = memberService.getMemberById(id);
        return ResponseEntity.ok(member);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Member> updateMember(@PathVariable Long id, @Valid @RequestBody Member member) {
        member.setId(id);
        Member updatedMember = memberService.createOrUpdateMember(member);
        return ResponseEntity.ok(updatedMember);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        memberService.deleteMember(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{memberId}/borrow/{bookId}")
    public ResponseEntity<Void> borrowBook(@PathVariable Long memberId, @PathVariable Long bookId) {
        memberService.borrowBook(memberId, bookId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{memberId}/return/{bookId}")
    public ResponseEntity<Void> returnBook(@PathVariable Long memberId, @PathVariable Long bookId) {
        memberService.returnBook(memberId, bookId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{name}/borrowed-books")
    public ResponseEntity<Set<Book>> getBorrowedBooksByMemberName(@PathVariable String name) {
        Set<Book> borrowedBooks = memberService.getBooksByMember(name);
        return ResponseEntity.ok(borrowedBooks);
    }

    @GetMapping("/distinct-borrowed-books")
    public ResponseEntity<List<String>> getDistinctBorrowedBookNames() {
        List<String> bookNames = memberService.getDistinctBorrowedBookNames();
        return ResponseEntity.ok(bookNames);
    }

    @GetMapping("/borrowed-books-summary")
    public ResponseEntity<Map<String, Long>> getBorrowedBooksSummary() {
        Map<String, Long> summary = memberService.getBorrowedBooksSummary();
        return ResponseEntity.ok(summary);
    }
}
