package com.syos.infrastructure.repository;

import com.syos.domain.model.Employee;

public interface UserRepository {

    Employee findByUsername(String username);

    boolean existsByUsername(String username);

    void clear();
}