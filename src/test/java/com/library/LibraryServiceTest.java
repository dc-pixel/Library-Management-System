package com.library;

import com.library.model.Loan;
import com.library.repository.LibraryRepository;
import com.library.model.Book;
import com.library.model.Member;
import com.library.service.LibraryService;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LibraryServiceTest {
    @Test
    void checkoutAndReturnMaintainsAvailability() throws IOException {
        LibraryRepository repo = new MemoryRepository();
        LibraryService service = new LibraryService(repo, 2);
        service.addBook("978-1", "Clean Code", "Robert Martin");
        service.registerMember("M1", "Dev", "dev@example.com");

        Loan loan = service.checkoutBook("978-1", "M1");
        assertTrue(loan.isActive());
        assertEquals(0, service.availableBookCount());

        service.returnBook("978-1", "M1");
        assertEquals(1, service.availableBookCount());
        assertEquals(0, service.activeLoanCount());
    }

    @Test
    void duplicateBookAndDuplicateMemberAreRejected() throws IOException {
        LibraryService service = new LibraryService(new MemoryRepository(), 2);
        service.addBook("978-1", "Book", "Author");
        assertThrows(IllegalArgumentException.class, () -> service.addBook("978-1", "Book 2", "Author 2"));
        service.registerMember("M1", "Dev", "dev@example.com");
        assertThrows(IllegalArgumentException.class, () -> service.registerMember("M1", "Other", "other@example.com"));
    }

    private static class MemoryRepository implements LibraryRepository {
        final List<Book> books = new ArrayList<>();
        final List<Member> members = new ArrayList<>();
        final List<Loan> loans = new ArrayList<>();
        public List<Book> loadBooks() { return new ArrayList<>(books); }
        public List<Member> loadMembers() { return new ArrayList<>(members); }
        public List<Loan> loadLoans() { return new ArrayList<>(loans); }
        public void save(List<Book> b, List<Member> m, List<Loan> l) { books.clear(); books.addAll(b); members.clear(); members.addAll(m); loans.clear(); loans.addAll(l); }
    }
}
