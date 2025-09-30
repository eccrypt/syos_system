package com.syos.service;

import com.syos.model.Product;

public interface ProductService {

    Product addProduct(String code, String name, double price);

    Product updateProductName(String code, String newName);

    Product findProductByCode(String code);
}
