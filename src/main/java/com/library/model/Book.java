package com.library.model;

import java.util.Objects;

public class Book {
    private final String isbn;
    private String title;
    private String author;
    private BookStatus status;

    public Book(String isbn, String title, String author) {
        this.isbn = require(isbn, "ISBN");
        this.title = require(title, "Title");
        this.author = require(author, "Author");
        this.status = BookStatus.AVAILABLE;
    }

    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public BookStatus getStatus() { return status; }

    public void updateDetails(String title, String author) {
        this.title = require(title, "Title");
        this.author = require(author, "Author");
    }

    public boolean isAvailable() { return status == BookStatus.AVAILABLE; }
    public void markBorrowed() { status = BookStatus.BORROWED; }
    public void markAvailable() { status = BookStatus.AVAILABLE; }

    private static String require(String value, String field) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(field + " cannot be blank.");
        return value.trim();
    }

    @Override
    public String toString() {
        return String.format("%s | %s | %s | %s", isbn, title, author, status);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book other)) return false;
        return isbn.equals(other.isbn);
    }

    @Override
    public int hashCode() { return Objects.hash(isbn); }
}
