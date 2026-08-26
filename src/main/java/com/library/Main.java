package com.library;

import com.library.repository.FileLibraryRepository;
import com.library.repository.LibraryRepository;
import com.library.service.LibraryService;
import com.library.ui.ConsoleUI;

import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        try {
            LibraryRepository repository = new FileLibraryRepository(Path.of("data", "library-data.txt"));
            LibraryService service = new LibraryService(repository, 5);
            new ConsoleUI(service).start();
        } catch (Exception e) {
            System.err.println("Unable to start Library Management System: " + e.getMessage());
            System.exit(1);
        }
    }
}
