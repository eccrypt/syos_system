package com.syos.infrastructure.repository;

import java.util.List;

import com.syos.domain.model.Product;

public interface ProductRepository {

    Product findByCode(String code);

    List<Product> findAll();

    void add(Product product);

    void update(Product product);

    void clear();
}
