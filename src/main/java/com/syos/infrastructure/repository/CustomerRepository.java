package com.syos.infrastructure.repository;

import com.syos.domain.model.Customer;

public interface CustomerRepository {

    void save(Customer customer);

    Customer findByEmail(String email);

    boolean existsByEmail(String email);

    void clear();
}
