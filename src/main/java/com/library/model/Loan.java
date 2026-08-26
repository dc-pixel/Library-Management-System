package com.library.model;

import java.time.LocalDate;
import java.util.Objects;

public class Loan {
    private final String isbn;
    private final String memberId;
    private final LocalDate checkoutDate;
    private LocalDate returnDate;

    public Loan(String isbn, String memberId, LocalDate checkoutDate) {
        this.isbn = require(isbn, "ISBN");
        this.memberId = require(memberId, "Member ID");
        this.checkoutDate = Objects.requireNonNull(checkoutDate, "Checkout date cannot be null.");
    }

    public String getIsbn() { return isbn; }
    public String getMemberId() { return memberId; }
    public LocalDate getCheckoutDate() { return checkoutDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public boolean isActive() { return returnDate == null; }

    public void markReturned(LocalDate date) {
        if (!isActive()) throw new IllegalStateException("Loan is already returned.");
        if (date.isBefore(checkoutDate)) throw new IllegalArgumentException("Return date cannot precede checkout date.");
        returnDate = date;
    }

    private static String require(String value, String field) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(field + " cannot be blank.");
        return value.trim();
    }

    @Override
    public String toString() {
        return String.format("%s | member=%s | borrowed=%s | returned=%s", isbn, memberId, checkoutDate, returnDate == null ? "-" : returnDate);
    }
}
