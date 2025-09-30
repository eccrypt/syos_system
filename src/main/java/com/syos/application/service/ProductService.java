package com.syos.application.service;

import com.syos.domain.model.Product;

public interface ProductService {

    Product addProduct(String code, String name, double price);

    Product updateProductName(String code, String newName);

    Product findProductByCode(String code);
}
