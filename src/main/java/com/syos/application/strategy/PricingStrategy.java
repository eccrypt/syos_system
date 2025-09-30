package com.syos.application.strategy;

import com.syos.domain.model.Product;

public interface PricingStrategy {
    double calculate(Product product, int quantity);
}
