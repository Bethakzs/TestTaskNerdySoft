package org.example.testtasknerdysoft.repo;

import org.example.testtasknerdysoft.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findByName(String name);

    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN TRUE ELSE FALSE END " +
            "FROM Member m JOIN m.borrowedBooks b WHERE b.id = :bookId")
    boolean existsByBorrowedBooks(Long bookId);
}
