package com.syos.infrastructure.repository;

import java.time.LocalDate;
import java.util.List;

import com.syos.domain.model.StockBatch;

public interface StockBatchRepository {

    List<StockBatch> findByProduct(String code);

    List<StockBatch> findByProductAllBatches(String code);

    void updateQuantity(int batchId, int newQty);

    void createBatch(String productCode, LocalDate purchaseDate, LocalDate expiryDate, int quantity);

    List<String> getAllProductCodesWithBatches();

    List<StockBatch> findExpiringBatches(String productCode, int daysThreshold);

    List<StockBatch> findAllExpiringBatches(int daysThreshold);

    StockBatch findById(int batchId);

    void setBatchQuantityToZero(int batchId);
}
