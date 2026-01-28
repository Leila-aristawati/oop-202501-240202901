package com.upb.agripos.model;

public class User {
    private int id;
    private final String username;
    private final String fullName;
    private final String role;

    public User(int id, String username, String fullName, String role) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public String getRole() {
        return role;
    }

    @Override
    public String toString() {
        return fullName + " (" + role + ")";
    }
}
