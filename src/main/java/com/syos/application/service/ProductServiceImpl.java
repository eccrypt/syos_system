package com.syos.application.service;

import com.syos.domain.exception.ValidationException;
import com.syos.domain.model.Product;
import com.syos.infrastructure.repository.ProductRepository;
import com.syos.infrastructure.util.ProductValidator;

public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product addProduct(String code, String name, double price) {
        ProductValidator.validateProductCode(code);
        ProductValidator.validateProductName(name);
        ProductValidator.validateProductPrice(price);

        if (productRepository.findByCode(code) != null) {
            throw new ValidationException("Product code already exists: " + code);
        }

        Product product = new Product.ProductBuilder().code(code).name(name).price(price).build();

        productRepository.add(product);

        return product;
    }

    @Override
    public Product updateProductName(String code, String newName) {
        ProductValidator.validateProductName(newName);

        Product existingProduct = productRepository.findByCode(code);
        if (existingProduct == null) {
            throw new ValidationException("Product with code " + code + " not found.");
        }

        Product updatedProduct = new Product.ProductBuilder()
                .code(existingProduct.getCode())
                .name(newName)
                .price(existingProduct.getPrice())
                .build();
        productRepository.update(updatedProduct);
        return updatedProduct;
    }

    @Override
    public Product findProductByCode(String code) {
        return productRepository.findByCode(code);
    }
}
