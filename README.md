# Library Management System

A modular Java console application for managing a library's catalog, members, checkouts, returns, and inventory availability.

## Features

- Add, remove, search, and list books
- Register, remove, search, and list members
- Borrow and return books with availability validation
- Track active loans and borrowing history
- Prevent duplicate ISBNs and duplicate member IDs
- Enforce a configurable maximum loan limit per member
- Persist data locally to a text file between runs
- Clean OOP design using encapsulation, abstraction, interfaces, and service classes

## Project Structure

```text
src/main/java/com/library/
├── Main.java
├── model/
│   ├── Book.java
│   ├── BookStatus.java
│   ├── Loan.java
│   └── Member.java
├── repository/
│   └── LibraryRepository.java
├── service/
│   └── LibraryService.java
└── ui/
    └── ConsoleUI.java
```

## Requirements

- Java 17+

## Run

```bash
javac -d out $(find src/main/java -name "*.java")
java -cp out com.library.Main
```

On Windows PowerShell:

```powershell
Get-ChildItem -Recurse src/main/java -Filter *.java | ForEach-Object { $_.FullName } | Set-Content sources.txt
javac -d out @sources.txt
java -cp out com.library.Main
```

The application stores its data in `data/library-data.txt` automatically.

## OOP Highlights

- **Encapsulation:** model state is private and accessed through methods.
- **Abstraction:** `LibraryRepository` defines persistence operations independently of storage details.
- **Separation of concerns:** models, persistence, business logic, and UI are isolated.
- **Composition:** `LibraryService` coordinates repositories and loan state.
- **Extensibility:** a database-backed repository or GUI can be added without rewriting core business rules.
