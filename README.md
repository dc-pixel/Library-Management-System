# Library Management System

A Java/OOP Library Management System with a modern responsive web dashboard.

## Features

- Book catalog and availability tracking
- Issue and return workflows
- Member management
- Search and filtering
- Browser persistence with localStorage
- Responsive dashboard UI
- Java 17 Maven console application preserved in `src/main/java`

## Web app

The Vercel frontend is a React + Vite application. Run locally:

```bash
npm install
npm run dev
```

Build for production:

```bash
npm run build
```

## Java application

The original Java/OOP implementation remains available under `src/main/java`:

```bash
mvn test
mvn exec:java
```

## Architecture

- `src/main/java/com/library/model` — domain entities
- `src/main/java/com/library/repository` — persistence abstraction
- `src/main/java/com/library/service` — business rules
- `src/main/java/com/library/ui` — console interface
- `src/main.jsx` — web dashboard
- `src/styles.css` — responsive UI

## Deployment

The web application is configured for Vercel with Vite. The browser demo stores its state in localStorage; a production multi-user version should connect the UI to a persistent API/database.

## OOP Highlights

- **Encapsulation:** model state is private and accessed through methods.
- **Abstraction:** repository interfaces separate persistence from business logic.
- **Separation of concerns:** models, persistence, services, and UI are isolated.
- **Extensibility:** a database-backed repository or REST API can be added without rewriting the core domain model.
