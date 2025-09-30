package com.syos.application.factory;

import com.syos.domain.model.BillItem;
import com.syos.domain.model.Product;
import com.syos.application.strategy.PricingStrategy;

public class BillItemFactory {
    private final PricingStrategy pricingStrategy;

    public BillItemFactory(PricingStrategy pricingStrategy) {
        this.pricingStrategy = pricingStrategy;
    }

    public BillItem create(Product product, int quantity) {
        return new BillItem.BillItemBuilder(product, quantity, pricingStrategy).build();
    }
}
