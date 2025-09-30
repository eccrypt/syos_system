package com.syos.strategy;

import java.util.Comparator;
import java.util.List;

import com.syos.model.StockBatch; 
import com.syos.model.ShelfStock; 

public interface ShelfStrategy {
	StockBatch selectBatchFromBackStore(List<StockBatch> batches);
	ShelfStock selectBatchFromShelf(List<ShelfStock> batches);
	Comparator<StockBatch> getStockBatchComparator();
	Comparator<ShelfStock> getShelfStockComparator();
}