package com.syos.application.strategy;

import java.util.Comparator;
import java.util.List;

import com.syos.domain.model.StockBatch; 
import com.syos.domain.model.ShelfStock; 

public interface ShelfStrategy {
	StockBatch selectBatchFromBackStore(List<StockBatch> batches);
	ShelfStock selectBatchFromShelf(List<ShelfStock> batches);
	Comparator<StockBatch> getStockBatchComparator();
	Comparator<ShelfStock> getShelfStockComparator();
}
