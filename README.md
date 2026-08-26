# 📚 Library Management System

A portfolio-ready Library Management System combining a **React + Vite web dashboard** with a **Java 17 OOP/Maven backend implementation**.

## ✨ Features

### Web dashboard
- Responsive dashboard with inventory statistics
- Book search and availability filtering
- Member management
- Add books and members with validation
- Issue and return workflow linked to the actual borrower
- Borrower visibility for issued books
- Persistent browser data with `localStorage`
- Demo-data reset for quick testing
- Mobile-friendly layout and accessible form controls

### Java application
- Book, member and loan domain models
- Repository abstraction for persistence
- Service layer for business rules
- Console UI
- JUnit 5 tests
- Maven build configuration

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Frontend | React 19, Vite 7, CSS |
| Backend/domain | Java 17 |
| Build | Maven, npm |
| Testing | JUnit 5 |
| Deployment | Vercel-ready static frontend |
| Storage (demo) | Browser localStorage |

## 🚀 Run locally

### Web application

```bash
npm install
npm run dev
```

Create a production build:

```bash
npm run build
```

### Java application

Requirements: **JDK 17+** and Maven.

```bash
mvn test
mvn exec:java
```

## 🧱 Architecture

```text
src/
├── main.jsx                 # React web application
├── styles.css               # Responsive dashboard styling
├── main/java/com/library/
│   ├── model/               # Book, Member, Loan, status models
│   ├── repository/          # Persistence abstraction/implementation
│   ├── service/             # Library business rules
│   └── ui/                  # Console UI
└── test/java/com/library/   # Unit tests
```

The Java implementation follows separation of concerns between **domain models, repository, service and UI**. The browser dashboard is intentionally local-first; a production multi-user deployment should replace localStorage with a persistent API/database.

## 🧪 Testing

Run the Java test suite with:

```bash
mvn test
```

The project also keeps frontend and Java concerns separate so each layer can evolve independently.

## ☁️ Deployment

The React/Vite frontend is configured for Vercel. The deployed demo is client-side and does not require an application server.

For a production version, recommended next steps are:

1. Add a REST API.
2. Move books, members and loans to PostgreSQL/MySQL.
3. Add authentication and role-based access.
4. Add server-side validation and audit history.

## 🎯 OOP concepts demonstrated

- **Encapsulation** — model state is protected behind domain methods.
- **Abstraction** — repository interfaces isolate persistence details.
- **Separation of concerns** — models, persistence, services and UI have distinct responsibilities.
- **Extensibility** — the repository layer can be replaced with a database-backed implementation without rewriting the core domain model.

## 📌 Project status

**Active portfolio project** — the web dashboard and Java OOP implementation are maintained as complementary parts of the system.

## License

Use and adapt this project according to the repository's existing source/license terms.
