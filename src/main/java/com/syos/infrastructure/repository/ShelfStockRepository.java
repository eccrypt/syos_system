package com.syos.infrastructure.repository;

import java.time.LocalDate;
import java.util.List;

import com.syos.domain.model.ShelfStock;

public interface ShelfStockRepository {

    int getQuantity(String productCode);

    void upsertQuantity(String productCode, int qty);

    void deductQuantity(String productCode, int qty);

    List<String> getAllProductCodes();

    List<ShelfStock> getBatchesOnShelf(String productCode);

    void updateBatchQuantityOnShelf(String productCode, int batchId, int quantity, LocalDate expiryDate);

    void deductQuantityFromBatchOnShelf(String productCode, int batchId, int quantity);

    void removeBatchFromShelf(String productCode, int batchId);

    ShelfStock findByCode(String productCode);
}
