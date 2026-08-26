package com.library.repository;

import com.library.model.Book;
import com.library.model.Loan;
import com.library.model.Member;

import java.io.IOException;
import java.util.List;

public interface LibraryRepository {
    List<Book> loadBooks() throws IOException;
    List<Member> loadMembers() throws IOException;
    List<Loan> loadLoans() throws IOException;
    void save(List<Book> books, List<Member> members, List<Loan> loans) throws IOException;
}
