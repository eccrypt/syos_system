package com.syos.repository;

import com.syos.model.Customer;

public interface CustomerRepository {

    void save(Customer customer);

    Customer findByEmail(String email);

    boolean existsByEmail(String email);

    void clear();
}