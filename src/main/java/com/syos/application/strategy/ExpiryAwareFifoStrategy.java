package com.syos.application.strategy;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import com.syos.domain.model.StockBatch;
import com.syos.infrastructure.util.CommonVariables;
import com.syos.domain.model.ShelfStock;

public class ExpiryAwareFifoStrategy implements ShelfStrategy {

	@Override
	public StockBatch selectBatchFromBackStore(List<StockBatch> batches) {
		if (batches == null || batches.isEmpty()) {
			return null;
		}
		LocalDate cutoff = LocalDate.now().plusWeeks(CommonVariables.discountExpiryWeeks);
		StockBatch bestSafe = batches.stream().filter(batch -> batch.getExpiryDate().isAfter(cutoff))
				.min(Comparator.comparing(StockBatch::getPurchaseDate).thenComparing(StockBatch::getExpiryDate))
				.orElse(null);

		if (bestSafe != null) {
			return bestSafe;
		}
		return batches.stream()
				.min(Comparator.comparing(StockBatch::getPurchaseDate).thenComparing(StockBatch::getExpiryDate))

				.orElse(null);
	}

	@Override
	public ShelfStock selectBatchFromShelf(List<ShelfStock> batches) {
		if (batches == null || batches.isEmpty()) {
			return null;
		}
		LocalDate cutoff = LocalDate.now().plusWeeks(CommonVariables.discountExpiryWeeks);
		ShelfStock bestSafe = batches.stream().filter(batch -> batch.getExpiryDate().isAfter(cutoff))
				.min(Comparator.comparing(ShelfStock::getExpiryDate).thenComparing(ShelfStock::getBatchId))
				.orElse(null);

		if (bestSafe != null) {
			return bestSafe;
		}
		return batches.stream()
				.min(Comparator.comparing(ShelfStock::getExpiryDate).thenComparing(ShelfStock::getBatchId))
				.orElse(null);
	}

	@Override
	public Comparator<StockBatch> getStockBatchComparator() {
		return Comparator.comparing(StockBatch::getPurchaseDate).thenComparing(StockBatch::getExpiryDate);
	}

	@Override
	public Comparator<ShelfStock> getShelfStockComparator() {
		return Comparator.comparing(ShelfStock::getExpiryDate).thenComparing(ShelfStock::getBatchId);
	}
}
