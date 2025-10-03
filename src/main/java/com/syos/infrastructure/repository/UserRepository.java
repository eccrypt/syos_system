package com.syos.infrastructure.repository;

import com.syos.domain.model.Employee;
import com.syos.domain.model.User;

public interface UserRepository {

    User findByEmail(String email);

    boolean existsByEmail(String email);

    void save(Employee employee);

}