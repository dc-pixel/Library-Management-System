package com.library.repository;

import com.library.model.Book;
import com.library.model.BookStatus;
import com.library.model.Loan;
import com.library.model.Member;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FileLibraryRepository implements LibraryRepository {
    private final Path file;

    public FileLibraryRepository(Path file) {
        this.file = file;
    }

    @Override
    public List<Book> loadBooks() throws IOException {
        List<Book> books = new ArrayList<>();
        for (String line : readLines("BOOK")) {
            String[] p = line.split("\\|", -1);
            if (p.length >= 4) {
                Book b = new Book(p[1], p[2], p[3]);
                if (BookStatus.BORROWED.name().equals(p[4])) b.markBorrowed();
                books.add(b);
            }
        }
        return books;
    }

    @Override
    public List<Member> loadMembers() throws IOException {
        List<Member> members = new ArrayList<>();
        for (String line : readLines("MEMBER")) {
            String[] p = line.split("\\|", -1);
            if (p.length >= 4) members.add(new Member(p[1], p[2], p[3]));
        }
        return members;
    }

    @Override
    public List<Loan> loadLoans() throws IOException {
        List<Loan> loans = new ArrayList<>();
        for (String line : readLines("LOAN")) {
            String[] p = line.split("\\|", -1);
            if (p.length >= 5) {
                Loan loan = new Loan(p[1], p[2], LocalDate.parse(p[3]));
                if (!p[4].isBlank()) loan.markReturned(LocalDate.parse(p[4]));
                loans.add(loan);
            }
        }
        return loans;
    }

    @Override
    public void save(List<Book> books, List<Member> members, List<Loan> loans) throws IOException {
        Path parent = file.getParent();
        if (parent != null) Files.createDirectories(parent);
        List<String> lines = new ArrayList<>();
        for (Book b : books) lines.add("BOOK|" + safe(b.getIsbn()) + "|" + safe(b.getTitle()) + "|" + safe(b.getAuthor()) + "|" + b.getStatus());
        for (Member m : members) lines.add("MEMBER|" + safe(m.getId()) + "|" + safe(m.getName()) + "|" + safe(m.getEmail()));
        for (Loan l : loans) lines.add("LOAN|" + safe(l.getIsbn()) + "|" + safe(l.getMemberId()) + "|" + l.getCheckoutDate() + "|" + (l.getReturnDate() == null ? "" : l.getReturnDate()));
        Files.write(file, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private List<String> readLines(String type) throws IOException {
        if (!Files.exists(file)) return List.of();
        return Files.readAllLines(file).stream().filter(l -> l.startsWith(type + "|")).toList();
    }

    private static String safe(String value) {
        return value.replace("|", "/").replace("\n", " ").replace("\r", " ");
    }
}
