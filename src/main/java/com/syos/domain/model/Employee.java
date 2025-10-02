package com.syos.domain.model;

import com.syos.domain.enums.UserType;

public class Employee extends User {
    private final String username;
    private final UserType role;

    public Employee(String username, String password, UserType role) {
        super(username, password); // using username as email for now
        this.username = username;
        this.role = role;
    }

    @Override
    public UserType getRole() {
        return role;
    }

    public String getUsername() {
        return username;
    }
}