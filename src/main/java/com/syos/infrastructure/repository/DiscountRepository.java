package com.syos.infrastructure.repository;

import java.time.LocalDate;
import java.util.List;

import com.syos.domain.enums.DiscountType;
import com.syos.domain.model.Discount;

public interface DiscountRepository {

    Discount findById(int discountId);

    List<Discount> findActiveDiscounts(String productCode, LocalDate date);

    int createDiscount(String discountName, DiscountType discountType, double discountValue, LocalDate startDate,
            LocalDate endDate);

    void linkProductToDiscount(String productCode, int discountId);

    List<Discount> findAll();

    List<Discount> findDiscountsByProductCode(String productCode, LocalDate asOfDate);

    boolean unassignDiscountFromProduct(String productCode, int discountId);
}
