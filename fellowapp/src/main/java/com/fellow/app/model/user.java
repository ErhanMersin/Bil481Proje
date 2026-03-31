package com.fellow.app.model;

import java.time.LocalDateTime;

public class User {
    private int id;
    private String name;
    private String email;
    private String department;
    private LocalDateTime registrationDate;

    public User() {
    }

    public User(String name, String email, String department) {
        this.name = name;
        this.email = email;
        this.department = department;
    }

    // --- Getters & Setters ---
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }
}