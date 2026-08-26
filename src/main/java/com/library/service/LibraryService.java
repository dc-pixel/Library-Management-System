package com.library.service;

import com.library.model.Book;
import com.library.model.Loan;
import com.library.model.Member;
import com.library.repository.LibraryRepository;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LibraryService {
    private final LibraryRepository repository;
    private final List<Book> books;
    private final List<Member> members;
    private final List<Loan> loans;
    private final int maxActiveLoans;

    public LibraryService(LibraryRepository repository, int maxActiveLoans) throws IOException {
        if (maxActiveLoans < 1) throw new IllegalArgumentException("Maximum loans must be at least 1.");
        this.repository = repository;
        this.maxActiveLoans = maxActiveLoans;
        this.books = new ArrayList<>(repository.loadBooks());
        this.members = new ArrayList<>(repository.loadMembers());
        this.loans = new ArrayList<>(repository.loadLoans());
    }

    public void addBook(String isbn, String title, String author) throws IOException {
        if (findBook(isbn) != null) throw new IllegalArgumentException("ISBN already exists.");
        books.add(new Book(isbn, title, author));
        persist();
    }

    public void removeBook(String isbn) throws IOException {
        Book book = requireBook(isbn);
        if (!book.isAvailable()) throw new IllegalStateException("Borrowed books cannot be removed.");
        books.remove(book);
        persist();
    }

    public List<Book> searchBooks(String query) {
        String q = query == null ? "" : query.trim().toLowerCase();
        return books.stream().filter(b -> q.isBlank() || b.getIsbn().toLowerCase().contains(q) || b.getTitle().toLowerCase().contains(q) || b.getAuthor().toLowerCase().contains(q)).sorted(Comparator.comparing(Book::getTitle)).toList();
    }

    public void registerMember(String id, String name, String email) throws IOException {
        if (findMember(id) != null) throw new IllegalArgumentException("Member ID already exists.");
        members.add(new Member(id, name, email));
        persist();
    }

    public void removeMember(String id) throws IOException {
        Member member = requireMember(id);
        if (activeLoans(id) > 0) throw new IllegalStateException("Member has active loans and cannot be removed.");
        members.remove(member);
        persist();
    }

    public List<Member> listMembers() {
        return members.stream().sorted(Comparator.comparing(Member::getName)).toList();
    }

    public Loan checkoutBook(String isbn, String memberId) throws IOException {
        Book book = requireBook(isbn);
        requireMember(memberId);
        if (!book.isAvailable()) throw new IllegalStateException("Book is currently borrowed.");
        if (activeLoans(memberId) >= maxActiveLoans) throw new IllegalStateException("Member reached the maximum active loan limit.");
        book.markBorrowed();
        Loan loan = new Loan(isbn, memberId, LocalDate.now());
        loans.add(loan);
        persist();
        return loan;
    }

    public Loan returnBook(String isbn, String memberId) throws IOException {
        Book book = requireBook(isbn);
        Loan loan = loans.stream().filter(l -> l.getIsbn().equalsIgnoreCase(isbn) && l.getMemberId().equalsIgnoreCase(memberId) && l.isActive()).findFirst().orElseThrow(() -> new IllegalStateException("No active loan found for this member and book."));
        loan.markReturned(LocalDate.now());
        book.markAvailable();
        persist();
        return loan;
    }

    public List<Loan> activeLoans() {
        return loans.stream().filter(Loan::isActive).sorted(Comparator.comparing(Loan::getCheckoutDate)).toList();
    }

    public List<Loan> memberLoans(String memberId) {
        return loans.stream().filter(l -> l.getMemberId().equalsIgnoreCase(memberId)).sorted(Comparator.comparing(Loan::getCheckoutDate).reversed()).toList();
    }

    public int activeLoans(String memberId) {
        return (int) loans.stream().filter(l -> l.getMemberId().equalsIgnoreCase(memberId) && l.isActive()).count();
    }

    public int bookCount() { return books.size(); }
    public int memberCount() { return members.size(); }
    public int availableBookCount() { return (int) books.stream().filter(Book::isAvailable).count(); }
    public int activeLoanCount() { return (int) loans.stream().filter(Loan::isActive).count(); }

    private Book findBook(String isbn) { return books.stream().filter(b -> b.getIsbn().equalsIgnoreCase(isbn)).findFirst().orElse(null); }
    private Member findMember(String id) { return members.stream().filter(m -> m.getId().equalsIgnoreCase(id)).findFirst().orElse(null); }
    private Book requireBook(String isbn) { if (isbn == null) throw new IllegalArgumentException("ISBN cannot be blank."); Book b = findBook(isbn); if (b == null) throw new IllegalArgumentException("Book not found."); return b; }
    private Member requireMember(String id) { if (id == null) throw new IllegalArgumentException("Member ID cannot be blank."); Member m = findMember(id); if (m == null) throw new IllegalArgumentException("Member not found."); return m; }
    private void persist() throws IOException { repository.save(books, members, loans); }
}
