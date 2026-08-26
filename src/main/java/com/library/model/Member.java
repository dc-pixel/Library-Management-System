package com.library.model;

import java.util.Objects;

public class Member {
    private final String id;
    private String name;
    private String email;

    public Member(String id, String name, String email) {
        this.id = require(id, "Member ID");
        this.name = require(name, "Name");
        this.email = require(email, "Email");
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }

    public void updateDetails(String name, String email) {
        this.name = require(name, "Name");
        this.email = require(email, "Email");
    }

    private static String require(String value, String field) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(field + " cannot be blank.");
        return value.trim();
    }

    @Override
    public String toString() {
        return String.format("%s | %s | %s", id, name, email);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Member other)) return false;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}
