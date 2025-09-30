package com.syos.domain.observer;

public interface StockObserver {
	void onStockLow(String productCode, int quantityRemaining);
}
