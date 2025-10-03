package com.syos.domain.model;

import java.sql.Timestamp;

import com.syos.domain.enums.UserType;

public class Customer extends User {
    private final String firstName;
    private final String lastName;
    private final UserType role;
    private final Timestamp createdDate;

    public Customer(String email, String password, String firstName, String lastName, UserType role, Timestamp createdDate) {
        super(email, password);
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.createdDate = createdDate;
    }

    @Override
    public UserType getRole() {
        return role;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Timestamp getCreatedDate() {
        return createdDate;
    }
}