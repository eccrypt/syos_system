package com.syos.application.strategy;

import com.syos.domain.model.Product;

public class NoDiscountStrategy implements PricingStrategy {
    @Override
    public double calculate(Product product, int quantity) {
        return product.getPrice() * quantity;
    }
}
