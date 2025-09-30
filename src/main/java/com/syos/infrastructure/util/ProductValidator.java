package com.syos.infrastructure.util;

import com.syos.domain.exception.ValidationException;

public class ProductValidator {

    public static void validateProductCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new ValidationException("Product code cannot be empty");
        }
        if (code.length() > CommonVariables.MAX_CODE_LENGTH) {
            throw new ValidationException("Product code must be at most " + CommonVariables.MAX_CODE_LENGTH + " characters");
        }
        if (!code.matches("^[a-zA-Z0-9]+$")) {
            throw new ValidationException("Product code can only contain letters and numbers");
        }
    }

    public static void validateProductName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Product name cannot be empty");
        }
        if (name.length() < CommonVariables.MIN_NAME_LENGTH || name.length() > CommonVariables.MAX_NAME_LENGTH) {
            throw new ValidationException("Product name must be between " + CommonVariables.MIN_NAME_LENGTH + " and " + CommonVariables.MAX_NAME_LENGTH + " characters long");
        }
    }

    public static void validateProductPrice(double price) {
        if (price < CommonVariables.MIN_PRICE) {
            throw new ValidationException("Price cannot be negative. Minimum price is " + CommonVariables.MIN_PRICE);
        }
    }
}
