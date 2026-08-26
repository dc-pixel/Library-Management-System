package com.library.ui;

import com.library.model.Book;
import com.library.model.Loan;
import com.library.model.Member;
import com.library.service.LibraryService;

import java.io.IOException;
import java.util.Scanner;

public class ConsoleUI {
    private final LibraryService service;
    private final Scanner scanner;

    public ConsoleUI(LibraryService service) {
        this.service = service;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("\n=== Library Management System ===");
        while (true) {
            showMenu();
            String choice = prompt("Choose an option: ");
            try {
                switch (choice) {
                    case "1" -> addBook();
                    case "2" -> listBooks();
                    case "3" -> searchBooks();
                    case "4" -> registerMember();
                    case "5" -> listMembers();
                    case "6" -> checkout();
                    case "7" -> returnBook();
                    case "8" -> activeLoans();
                    case "9" -> memberHistory();
                    case "10" -> removeBook();
                    case "11" -> removeMember();
                    case "0" -> { System.out.println("Goodbye!"); return; }
                    default -> System.out.println("Invalid option.");
                }
            } catch (IOException | IllegalArgumentException | IllegalStateException ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
    }

    private void showMenu() {
        System.out.printf("\nBooks: %d (%d available) | Members: %d | Active loans: %d%n", service.bookCount(), service.availableBookCount(), service.memberCount(), service.activeLoanCount());
        System.out.println("1. Add book");
        System.out.println("2. List books");
        System.out.println("3. Search books");
        System.out.println("4. Register member");
        System.out.println("5. List members");
        System.out.println("6. Checkout book");
        System.out.println("7. Return book");
        System.out.println("8. View active loans");
        System.out.println("9. View member history");
        System.out.println("10. Remove book");
        System.out.println("11. Remove member");
        System.out.println("0. Exit");
    }

    private void addBook() throws IOException {
        service.addBook(prompt("ISBN: "), prompt("Title: "), prompt("Author: "));
        System.out.println("Book added successfully.");
    }

    private void listBooks() {
        service.searchBooks("").forEach(System.out::println);
        if (service.bookCount() == 0) System.out.println("No books found.");
    }

    private void searchBooks() {
        String q = prompt("Search by ISBN, title, or author: ");
        var results = service.searchBooks(q);
        results.forEach(System.out::println);
        System.out.println("Matches: " + results.size());
    }

    private void registerMember() throws IOException {
        service.registerMember(prompt("Member ID: "), prompt("Name: "), prompt("Email: "));
        System.out.println("Member registered successfully.");
    }

    private void listMembers() {
        service.listMembers().forEach(System.out::println);
        if (service.memberCount() == 0) System.out.println("No members found.");
    }

    private void checkout() throws IOException {
        Loan loan = service.checkoutBook(prompt("ISBN: "), prompt("Member ID: "));
        System.out.println("Checkout successful: " + loan);
    }

    private void returnBook() throws IOException {
        Loan loan = service.returnBook(prompt("ISBN: "), prompt("Member ID: "));
        System.out.println("Return successful: " + loan);
    }

    private void activeLoans() {
        var loans = service.activeLoans();
        loans.forEach(System.out::println);
        System.out.println("Active loans: " + loans.size());
    }

    private void memberHistory() {
        String id = prompt("Member ID: ");
        Member member = service.listMembers().stream().filter(m -> m.getId().equalsIgnoreCase(id)).findFirst().orElseThrow(() -> new IllegalArgumentException("Member not found."));
        System.out.println(member);
        service.memberLoans(id).forEach(System.out::println);
    }

    private void removeBook() throws IOException {
        service.removeBook(prompt("ISBN: "));
        System.out.println("Book removed successfully.");
    }

    private void removeMember() throws IOException {
        service.removeMember(prompt("Member ID: "));
        System.out.println("Member removed successfully.");
    }

    private String prompt(String message) {
        System.out.print(message);
        String value = scanner.nextLine().trim();
        if (value.isBlank()) throw new IllegalArgumentException("Input cannot be blank.");
        return value;
    }
}
